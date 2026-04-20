package com.wdcftgg.farmersdelightlegacy.common.compat.crafttweaker;

import com.wdcftgg.farmersdelightlegacy.common.recipe.CuttingBoardRecipeManager;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.farmersdelight.CuttingBoard")
public final class ZenCuttingBoardRecipes {

    private ZenCuttingBoardRecipes() {
    }

    @ZenMethod
    public static boolean addRecipe(String key, IIngredient[] inputIngredients, IItemStack[] resultStacks) {
        return addRecipeAdvanced(key, inputIngredients, new IIngredient[0], resultStacks, null);
    }

    @ZenMethod
    public static boolean addRecipeAdvanced(String key, IIngredient[] inputIngredients, IIngredient[] toolIngredients,
                                            IItemStack[] resultStacks, float[] resultChances) {
        String[] inputTokens = CraftTweakerCompatHelper.toIngredientTokens(inputIngredients);
        String[] toolTokens = CraftTweakerCompatHelper.toIngredientTokens(toolIngredients);
        if (toolTokens == null || toolTokens.length == 0) {
            toolTokens = new String[]{"ore:toolKnife"};
        }

        if (resultStacks == null || resultStacks.length == 0) {
            return false;
        }
        String[] resultTokens = new String[resultStacks.length];
        int[] resultCounts = new int[resultStacks.length];
        for (int index = 0; index < resultStacks.length; index++) {
            IItemStack resultStack = resultStacks[index];
            String itemId = CraftTweakerCompatHelper.itemIdOf(resultStack);
            net.minecraft.item.ItemStack nativeResult = CraftTweakerCompatHelper.stackOf(resultStack);
            if (itemId == null || nativeResult.isEmpty()) {
                return false;
            }
            resultTokens[index] = itemId;
            resultCounts[index] = Math.max(1, nativeResult.getCount());
        }

        if (inputTokens == null || inputTokens.length == 0) {
            return false;
        }
        return CuttingBoardRecipeManager.registerScriptRecipe(key, inputTokens, toolTokens, resultTokens, resultCounts, resultChances);
    }

    @ZenMethod
    public static boolean addRecipe(String key, String[] inputTokens, String[] resultTokens) {
        return addRecipeAdvanced(key, inputTokens, new String[]{"ore:toolKnife"}, resultTokens, null, null);
    }

    @ZenMethod
    public static boolean addRecipeAdvanced(String key, String[] inputTokens, String[] toolTokens,
                                            String[] resultTokens, int[] resultCounts, float[] resultChances) {
        return CuttingBoardRecipeManager.registerScriptRecipe(key, inputTokens, toolTokens, resultTokens, resultCounts, resultChances);
    }

    @ZenMethod
    public static boolean removeRecipe(String key) {
        return CuttingBoardRecipeManager.unregisterScriptRecipe(key);
    }
}

