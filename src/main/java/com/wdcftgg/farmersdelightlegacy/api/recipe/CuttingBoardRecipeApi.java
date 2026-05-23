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

    /**
     * Registers a runtime recipe.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param inputTokens Input ingredient token strings accepted by the cutting board recipe parser.
     * @param toolTokens Tool token strings accepted by the cutting board recipe parser; {@code null} uses the default knife tool and an empty array requires no tool.
     * @param resultTokens Result item token strings produced by the cutting board recipe.
     * @param resultCounts Result stack sizes aligned by index with {@code resultTokens}.
     * @param resultChances Result chances aligned by index with {@code resultTokens}, using the 0.0 to 1.0 range.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipe(String key, String[] inputTokens, String[] toolTokens,
                                         String[] resultTokens, int[] resultCounts, float[] resultChances) {
        return CuttingBoardRecipeManager.registerScriptRecipe(key, inputTokens, toolTokens,
                resultTokens, resultCounts, resultChances);
    }

    /**
     * Registers a runtime recipe.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param inputToken The single input ingredient token string accepted by the cutting board recipe parser.
     * @param toolToken The single tool token string; {@code null} uses the default knife tool.
     * @param resultToken The single result item token string.
     * @param resultCount The result stack size.
     * @param resultChance The result chance, using the 0.0 to 1.0 range.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipe(String key, String inputToken, String toolToken, String resultToken,
                                         int resultCount, float resultChance) {
        String[] toolTokens = toolToken == null ? null : new String[] {toolToken};
        return registerRecipe(key, new String[] {inputToken}, toolTokens, new String[] {resultToken},
                new int[] {resultCount}, new float[] {resultChance});
    }

    /**
     * Registers a runtime recipe with concrete item stacks.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param inputStacks Input item stacks accepted by the recipe; stack size is ignored for matching.
     * @param toolStacks Tool item stacks accepted by the recipe; {@code null} uses the default knife tool and an empty array requires no tool.
     * @param resultStacks Result item stacks produced by the recipe, preserving stack size and metadata.
     * @param resultChances Result chances aligned by index with {@code resultStacks}, using the 0.0 to 1.0 range.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipe(String key, ItemStack[] inputStacks, ItemStack[] toolStacks,
                                         ItemStack[] resultStacks, float[] resultChances) {
        return CuttingBoardRecipeManager.registerScriptRecipe(key, inputStacks, toolStacks, resultStacks, resultChances);
    }

    /**
     * Registers a runtime recipe with concrete item stacks.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param inputStack The input item stack accepted by the recipe; stack size is ignored for matching.
     * @param toolStack The tool item stack accepted by the recipe; {@link ItemStack#EMPTY} requires no tool.
     * @param resultStack The result item stack produced by the recipe.
     * @param resultChance The result chance, using the 0.0 to 1.0 range.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipe(String key, ItemStack inputStack, ItemStack toolStack, ItemStack resultStack,
                                         float resultChance) {
        ItemStack[] toolStacks = toolStack == null ? null : toolStack.isEmpty() ? new ItemStack[0] : new ItemStack[] {toolStack};
        return registerRecipe(key, new ItemStack[] {inputStack}, toolStacks, new ItemStack[] {resultStack},
                new float[] {resultChance});
    }

    /**
     * Unregisters a recipe by key.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @return The result produced by this API method.
     */
    public static boolean unregisterRecipe(String key) {
        return CuttingBoardRecipeManager.unregisterScriptRecipe(key);
    }

    /**
     * Removes recipes that produce the supplied output.
     *
     * @param outputStack The output stack to register, remove, match, or display.
     * @return The result produced by this API method.
     */
    public static int removeRecipesByOutput(ItemStack outputStack) {
        return CuttingBoardRecipeManager.removeRecipesByOutput(outputStack);
    }

    /**
     * Checks whether a cutting board recipe exists.
     *
     * @param inputStack The input stack to check or process.
     * @param toolStack The tool stack to check or use for processing.
     * @return The result produced by this API method.
     */
    public static boolean hasRecipe(ItemStack inputStack, ItemStack toolStack) {
        return CuttingBoardRecipeManager.hasRecipe(inputStack, toolStack);
    }

    /**
     * Gets cutting board results for the supplied input and tool.
     *
     * @param inputStack The input stack to check or process.
     * @param toolStack The tool stack to check or use for processing.
     * @param random The random source used for chance-based result rolls.
     * @return The result produced by this API method.
     */
    public static List<ItemStack> getProcessedResults(ItemStack inputStack, ItemStack toolStack, Random random) {
        return CuttingBoardRecipeManager.getProcessedResults(inputStack, toolStack, random);
    }

    /**
     * Returns the registered recipe views.
     *
     * @return The result produced by this API method.
     */
    public static List<CuttingBoardRecipeManager.CuttingBoardRecipeView> getRecipes() {
        return CuttingBoardRecipeManager.getRecipes();
    }
}
