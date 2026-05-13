package com.wdcftgg.farmersdelightlegacy.client.jei;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public final class DecompositionRecipeCategory implements IRecipeCategory<DecompositionJeiRecipe> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(FarmersDelightLegacy.MOD_ID, "textures/gui/jei/decomposition.png");
    private static final int SLOT_SIZE = 22;

    private final IDrawable background;
    private final IDrawable slotIcon;
    private final IDrawable icon;

    public DecompositionRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 118, 80);
        this.slotIcon = guiHelper.createDrawable(TEXTURE, 119, 0, SLOT_SIZE, SLOT_SIZE);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.RICH_SOIL));
    }

    @Override
    public String getUid() {
        return JeiUids.DECOMPOSITION;
    }

    @Override
    public String getTitle() {
        return I18n.format("farmersdelight.jei.decomposition");
    }

    @Override
    public String getModName() {
        return FarmersDelightLegacy.MOD_NAME;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, DecompositionJeiRecipe recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 8, 25);
        recipeLayout.getItemStacks().init(1, false, 92, 25);
        recipeLayout.getItemStacks().init(2, true, 63, 53);
        recipeLayout.getItemStacks().set(ingredients);
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        this.slotIcon.draw(minecraft, 63, 53);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (isCursorInsideBounds(40, 38, 11, 11, mouseX, mouseY)) {
            return Collections.singletonList(I18n.format("farmersdelight.jei.decomposition.light"));
        }
        if (isCursorInsideBounds(53, 38, 11, 11, mouseX, mouseY)) {
            return Collections.singletonList(I18n.format("farmersdelight.jei.decomposition.fluid"));
        }
        if (isCursorInsideBounds(67, 38, 11, 11, mouseX, mouseY)) {
            return Collections.singletonList(I18n.format("farmersdelight.jei.decomposition.accelerators"));
        }
        return Collections.emptyList();
    }

    private static boolean isCursorInsideBounds(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}