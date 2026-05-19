package com.wdcftgg.farmersdelightlegacy.client.jei;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public final class HuntingDropRecipeCategory implements IRecipeCategory<HuntingDropJeiRecipe> {

    private static final ResourceLocation cuttingBoardTexture = new ResourceLocation(FarmersDelightLegacy.MOD_ID, "textures/gui/jei/cutting_board.png");
    private static final ResourceLocation iconTexture = new ResourceLocation(FarmersDelightLegacy.MOD_ID,
            "textures/gui/jei/icon/hunting_drops.png");

    private static final int backgroundWidth = 156;
    private static final int backgroundHeight = 80;
    private static final int outputSlotStep = 19;
    private static final int arrowX = 73;
    private static final int arrowY = 30;

    private final IDrawable background;
    private final IDrawable slot;
    private final IDrawable slotChance;
    private final IDrawable arrow;
    private final IDrawable icon;

    public HuntingDropRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(backgroundWidth, backgroundHeight);
        this.slot = guiHelper.createDrawable(cuttingBoardTexture, 0, 58, 18, 18);
        this.slotChance = guiHelper.createDrawable(cuttingBoardTexture, 18, 58, 18, 18);
        this.arrow = guiHelper.createDrawable(cuttingBoardTexture, 48, 21, 22, 16);
        this.icon = guiHelper.createDrawable(iconTexture, 0, 0, 20, 20, 20, 20);
    }

    @Override
    public String getUid() {
        return JeiUids.HUNTING_DROPS;
    }

    @Override
    public String getTitle() {
        return I18n.format("farmersdelight.jei.hunting_drops");
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
    public void setRecipe(IRecipeLayout recipeLayout, HuntingDropJeiRecipe recipeWrapper, IIngredients ingredients) {
        int outputCount = recipeWrapper.getOutputCount();
        for (int outputIndex = 0; outputIndex < outputCount; outputIndex++) {
            recipeLayout.getItemStacks().init(outputIndex, false,
                    recipeWrapper.getOutputSlotX(outputIndex), recipeWrapper.getOutputSlotY(outputIndex));
        }

        recipeLayout.getItemStacks().addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (input || slotIndex < 0 || slotIndex >= recipeWrapper.getOutputCount() || ingredient == null || ingredient.isEmpty()) {
                return;
            }
            float chance = recipeWrapper.getOutputChance(slotIndex);
            if (chance < 1.0F) {
                String chancePercent = chance < 0.01F ? "<1" : Integer.toString((int) (chance * 100.0F));
                tooltip.add(1, TextFormatting.GOLD + I18n.format("farmersdelight.jei.chance", chancePercent));
            }
            JeiTooltipUtil.addRecipeIdTooltip(tooltip, recipeWrapper.getRecipeId());
        });
        recipeWrapper.bindOutputSlots(recipeLayout.getItemStacks(), this.slot, this.slotChance);
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        this.arrow.draw(minecraft, arrowX, arrowY);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return java.util.Collections.emptyList();
    }
}
