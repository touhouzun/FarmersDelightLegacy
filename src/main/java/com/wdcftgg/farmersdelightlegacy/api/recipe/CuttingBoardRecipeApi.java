package com.wdcftgg.farmersdelightlegacy.api.recipe;

import com.wdcftgg.farmersdelightlegacy.common.recipe.CuttingBoardRecipeManager;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Random;

/**
 * Cutting Board recipe registration API.
 * <p>
 * inputTokens, toolTokens, and resultTokens support {@code modid:item}, {@code modid:item:meta}, and {@code ore:oreName}.
 * Passing {@code null} for toolTokens uses the default {@code ore:toolKnife}; passing an empty array means no tool is required.
 */
public final class CuttingBoardRecipeApi {

    private CuttingBoardRecipeApi() {
    }

    public static boolean registerRecipe(String key, String[] inputTokens, String[] toolTokens,
                                         String[] resultTokens, int[] resultCounts, float[] resultChances) {
        return CuttingBoardRecipeManager.registerScriptRecipe(key, inputTokens, toolTokens,
                resultTokens, resultCounts, resultChances);
    }

    public static boolean registerRecipe(String key, String inputToken, String toolToken, String resultToken,
                                         int resultCount, float resultChance) {
        String[] toolTokens = toolToken == null ? null : new String[] {toolToken};
        return registerRecipe(key, new String[] {inputToken}, toolTokens, new String[] {resultToken},
                new int[] {resultCount}, new float[] {resultChance});
    }

    public static boolean unregisterRecipe(String key) {
        return CuttingBoardRecipeManager.unregisterScriptRecipe(key);
    }

    public static int removeRecipesByOutput(ItemStack outputStack) {
        return CuttingBoardRecipeManager.removeRecipesByOutput(outputStack);
    }

    public static boolean hasRecipe(ItemStack inputStack, ItemStack toolStack) {
        return CuttingBoardRecipeManager.hasRecipe(inputStack, toolStack);
    }

    public static List<ItemStack> getProcessedResults(ItemStack inputStack, ItemStack toolStack, Random random) {
        return CuttingBoardRecipeManager.getProcessedResults(inputStack, toolStack, random);
    }

    public static List<CuttingBoardRecipeManager.CuttingBoardRecipeView> getRecipes() {
        return CuttingBoardRecipeManager.getRecipes();
    }
}
