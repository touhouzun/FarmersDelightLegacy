package com.wdcftgg.farmersdelightlegacy.api.recipe;

import com.wdcftgg.farmersdelightlegacy.common.recipe.CookingPotRecipe;
import com.wdcftgg.farmersdelightlegacy.common.recipe.CookingPotRecipeManager;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Cooking Pot recipe registration API.
 * <p>
 * ingredientTokens support {@code modid:item}, {@code modid:item@meta}, and {@code ore:oreName}.
 */
public final class CookingPotRecipeApi {

    private CookingPotRecipeApi() {
    }

    /**
     * Registers a runtime recipe.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param ingredientTokens Ingredient token strings accepted by the recipe parser, such as {@code modid:item}, {@code modid:item@meta}, or {@code ore:name}.
     * @param resultStack The item stack produced by the recipe.
     * @param outputContainer The container stack returned by the cooking pot recipe; an empty stack means no container is returned.
     * @param cookTime The cooking time in ticks.
     * @param experience The experience awarded when the recipe is taken.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipe(String key, String[] ingredientTokens, ItemStack resultStack,
                                         ItemStack outputContainer, int cookTime, float experience) {
        return CookingPotRecipeManager.registerScriptRecipe(key, ingredientTokens, resultStack, outputContainer,
                cookTime, experience, !outputContainer.isEmpty());
    }

    /**
     * Registers a runtime recipe.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param ingredientTokens Ingredient token strings accepted by the recipe parser, such as {@code modid:item}, {@code modid:item@meta}, or {@code ore:name}.
     * @param resultStack The item stack produced by the recipe.
     * @param cookTime The cooking time in ticks.
     * @param experience The experience awarded when the recipe is taken.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipe(String key, String[] ingredientTokens, ItemStack resultStack,
                                         int cookTime, float experience) {
        return CookingPotRecipeManager.registerScriptRecipe(key, ingredientTokens, resultStack, ItemStack.EMPTY,
                cookTime, experience, false);
    }

    /**
     * Unregisters a recipe by key.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @return The result produced by this API method.
     */
    public static boolean unregisterRecipe(String key) {
        return CookingPotRecipeManager.unregisterScriptRecipe(key);
    }

    /**
     * Removes recipes that produce the supplied output.
     *
     * @param outputStack The output stack to register, remove, match, or display.
     * @return The result produced by this API method.
     */
    public static int removeRecipesByOutput(ItemStack outputStack) {
        return CookingPotRecipeManager.removeRecipesByOutput(outputStack);
    }

    /**
     * Returns the registered recipe views.
     *
     * @return The result produced by this API method.
     */
    public static List<CookingPotRecipe> getRecipes() {
        return CookingPotRecipeManager.getRecipes();
    }
}
