package com.wdcftgg.farmersdelightlegacy.client.jei;

import com.google.common.collect.ImmutableList;
import com.wdcftgg.farmersdelightlegacy.api.recipe.knife.HuntingDropOutput;
import com.wdcftgg.farmersdelightlegacy.common.recipe.HuntingDropRecipeManager;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HuntingDropJeiRecipe implements IRecipeWrapper {

    private static final double ENTITY_BOX_WIDTH = 30.0D;
    private static final double ENTITY_BOX_HEIGHT = 20.0D;
    private static final int OUTPUT_GRID_X = 98;
    private static final int OUTPUT_GRID_Y = 19;
    private static final int OUTPUT_GRID_COLUMNS = 3;
    private static final int OUTPUT_GRID_MAX_SLOTS = 9;
    private static final int OUTPUT_STEP = 19;

    private final String recipeId;
    private final ResourceLocation entityId;
    private final List<ItemStack> toolOptions;
    private final List<HuntingDropOutput> outputs;
    private final List<ItemStack> outputStacks;
    private final boolean burningVariant;

    private World currentWorld;
    private EntityLivingBase entityInstance;
    private boolean entityErrored;
    private double renderScale = -1.0D;

    private HuntingDropJeiRecipe(String recipeId, ResourceLocation entityId, List<ItemStack> toolOptions,
                                 List<HuntingDropOutput> outputs, boolean burningVariant) {
        this.recipeId = recipeId;
        this.entityId = entityId;
        this.toolOptions = ImmutableList.copyOf(toolOptions);
        this.outputs = ImmutableList.copyOf(outputs);
        this.outputStacks = buildOutputStacks(outputs);
        this.burningVariant = burningVariant;
    }

    public static HuntingDropJeiRecipe of(HuntingDropRecipeManager.HuntingDropRecipeView recipe) {
        return new HuntingDropJeiRecipe(recipe.getKey(), recipe.getEntityId(), recipe.getToolOptions(),
                recipe.getOutputs(), recipe.isBurningVariant());
    }

    private static List<ItemStack> buildOutputStacks(List<HuntingDropOutput> outputs) {
        List<ItemStack> stacks = new ArrayList<>();
        for (HuntingDropOutput output : outputs) {
            stacks.add(output.getOutputStack());
        }
        return ImmutableList.copyOf(stacks);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setOutputs(VanillaTypes.ITEM, this.outputStacks);
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        refreshEntityInstance(minecraft);
        drawEntity(minecraft);
        drawAnimatedTool(minecraft);
        drawText(minecraft);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (isCursorInsideBounds(19, 8, 33, 49, mouseX, mouseY)) {
            if (this.entityInstance != null) {
                ModContainer mod = Loader.instance().getIndexedModList().get(this.entityId.getNamespace());
                return ImmutableList.of(
                        this.entityInstance.getName(),
                        TextFormatting.BLUE + "" + TextFormatting.ITALIC + (mod == null ? "Unknown" : mod.getName())
                );
            }
            return Collections.singletonList(this.entityId == null ? "Unknown" : this.entityId.toString());
        }
        return Collections.emptyList();
    }

    public int getOutputCount() {
        return Math.min(this.outputStacks.size(), OUTPUT_GRID_MAX_SLOTS);
    }

    public int getOutputSlotX(int outputIndex) {
        int size = getOutputCount();
        int columns = Math.min(size, OUTPUT_GRID_COLUMNS);
        int centerX = columns <= 1 ? OUTPUT_STEP : columns == 2 ? 10 : 1;
        return OUTPUT_GRID_X + centerX + ((outputIndex % OUTPUT_GRID_COLUMNS) * OUTPUT_STEP);
    }

    public int getOutputSlotY(int outputIndex) {
        int size = getOutputCount();
        int rows = (size + OUTPUT_GRID_COLUMNS - 1) / OUTPUT_GRID_COLUMNS;
        int centerY = rows <= 1 ? 10 : rows == 2 ? 1 : -8;
        return OUTPUT_GRID_Y + centerY + ((outputIndex / OUTPUT_GRID_COLUMNS) * OUTPUT_STEP);
    }

    public float getOutputChance(int outputIndex) {
        if (outputIndex < 0 || outputIndex >= this.outputs.size()) {
            return 1.0F;
        }
        return this.outputs.get(outputIndex).getChance();
    }

    public String getRecipeId() {
        return this.recipeId;
    }

    private void refreshEntityInstance(Minecraft minecraft) {
        if (minecraft.world != null && minecraft.world != this.currentWorld) {
            this.currentWorld = minecraft.world;
            this.entityInstance = null;
            this.entityErrored = false;
            this.renderScale = -1.0D;
        }

        if (this.entityInstance != null || this.entityErrored || minecraft.world == null || this.entityId == null) {
            return;
        }

        try {
            Entity entity = EntityList.createEntityByIDFromName(this.entityId, minecraft.world);
            if (entity instanceof EntityLivingBase) {
                this.entityInstance = (EntityLivingBase) entity;
            } else {
                this.entityErrored = true;
            }
        } catch (Exception ignored) {
            this.entityErrored = true;
        }
    }

    private void drawEntity(Minecraft minecraft) {
        if (this.entityInstance == null) {
            return;
        }

        forceAdultDisplay(this.entityInstance);
        if (this.burningVariant) {
            this.entityInstance.setFire(1);
        } else {
            this.entityInstance.extinguish();
        }

        if (this.renderScale < 0.0D) {
            double width = Math.max(0.1D, this.entityInstance.width);
            double height = Math.max(0.1D, this.entityInstance.height);
            this.renderScale = Math.min(ENTITY_BOX_WIDTH / width, ENTITY_BOX_HEIGHT / height);
        }

        GlStateManager.enableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.translate(35, 52, 0);
        GuiInventory.drawEntityOnScreen(0, 0, (int) Math.round(this.renderScale), -100, 0, this.entityInstance);
        GlStateManager.popMatrix();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    private void drawAnimatedTool(Minecraft minecraft) {
        ItemStack toolStack = getAnimatedTool(minecraft);
        if (toolStack.isEmpty()) {
            return;
        }

        long time = minecraft.world == null ? Minecraft.getSystemTime() : minecraft.world.getTotalWorldTime();
        float partialTicks = minecraft.world == null ? 0.0F : minecraft.getRenderPartialTicks();
        float theta = (float) (Math.PI / 2 * ((time + partialTicks) / 10.0F + 5.0F));
        double movementX = Math.sin(theta);
        double movementY = Math.cos(theta);
        double x = 46.0D + movementX * 10.0D;
        double y = 42.0D + movementY * 6.0D;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.rotate((float) (120.0D * movementX), 0, 0, 1);
        GlStateManager.disableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        minecraft.getRenderItem().renderItemAndEffectIntoGUI(toolStack, 0, -16);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    private void drawText(Minecraft minecraft) {
        int lineY = 71;
        if (this.burningVariant) {
            minecraft.fontRenderer.drawString(I18n.format("farmersdelight.jei.hunting_drops.burning"), 40, lineY, 0xAA3333);
        }
    }

    private ItemStack getAnimatedTool(Minecraft minecraft) {
        if (this.toolOptions.isEmpty()) {
            return ItemStack.EMPTY;
        }
        long time = minecraft.world == null ? Minecraft.getSystemTime() : minecraft.world.getTotalWorldTime();
        return this.toolOptions.get((int) ((time / 20L) % this.toolOptions.size()));
    }

    private static void forceAdultDisplay(EntityLivingBase entity) {
        if (entity instanceof EntityAgeable) {
            ((EntityAgeable) entity).setGrowingAge(0);
        }
        if (entity instanceof EntityZombie) {
            ((EntityZombie) entity).setChild(false);
        }
    }

    private static boolean isCursorInsideBounds(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}