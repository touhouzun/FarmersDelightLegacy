package com.wdcftgg.farmersdelightlegacy.api.recipe.knife;

import com.wdcftgg.farmersdelightlegacy.common.recipe.HarvestDropRecipeManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Knife harvest drop registration API.
 * <p>
 * Recipes run when a block is harvested with a knife. Each HuntingDropOutput controls its own chance and Fortune bonus.
 */
public final class HarvestDropRecipeApi {

    private HarvestDropRecipeApi() {
    }

    /**
     * Registers a runtime recipe.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputStack The output stack to register, remove, match, or display.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipe(String key, HarvestDropRecipeManager.HarvestTargetMatcher targetMatcher, ItemStack outputStack) {
        return HarvestDropRecipeManager.registerRecipe(key, targetMatcher, outputStack);
    }

    /**
     * Registers a recipe that is shown only in JEI.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputStack The output stack to register, remove, match, or display.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipeJei(String key, HarvestDropRecipeManager.HarvestTargetMatcher targetMatcher, ItemStack outputStack) {
        return HarvestDropRecipeManager.registerRecipeJei(key, targetMatcher, outputStack);
    }

    /**
     * Registers a runtime recipe.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputStack The output stack to register, remove, match, or display.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipe(String key, HarvestDropRecipeManager.HarvestTargetMatcher targetMatcher,
                                         ItemStack outputStack, boolean preventDuplicateStacking) {
        return HarvestDropRecipeManager.registerRecipe(key, targetMatcher, outputStack, preventDuplicateStacking);
    }

    /**
     * Registers a recipe that is shown only in JEI.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputStack The output stack to register, remove, match, or display.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipeJei(String key, HarvestDropRecipeManager.HarvestTargetMatcher targetMatcher,
                                            ItemStack outputStack, boolean preventDuplicateStacking) {
        return HarvestDropRecipeManager.registerRecipeJei(key, targetMatcher, outputStack, preventDuplicateStacking);
    }

    /**
     * Registers a runtime recipe.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputStack The output stack to register, remove, match, or display.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param chance The base chance for this output or effect, using the 0.0 to 1.0 range.
     * @param fortuneBonus The additional drop chance added per Fortune level.
     * @param displayBlockState The single block state rendered in the harvest drop JEI preview.
     * @param displaySupportBlockState The optional support block rendered below each occupied preview column; {@code null} renders no support block.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipe(String key, HarvestDropRecipeManager.HarvestTargetMatcher targetMatcher,
                                         ItemStack outputStack, boolean preventDuplicateStacking,
                                         float chance, float fortuneBonus, IBlockState displayBlockState,
                                         IBlockState displaySupportBlockState) {
        return HarvestDropRecipeManager.registerRecipe(key, targetMatcher, outputStack, preventDuplicateStacking,
                chance, fortuneBonus, displayBlockState, displaySupportBlockState);
    }

    /**
     * Registers a runtime recipe.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputStack The output stack to register, remove, match, or display.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param chance The base chance for this output or effect, using the 0.0 to 1.0 range.
     * @param fortuneBonus The additional drop chance added per Fortune level.
     * @param displayBlockStates The block states rendered in the harvest drop JEI preview, including relative offsets for multi-block previews.
     * @param displaySupportBlockState The optional support block rendered below each occupied preview column; {@code null} renders no support block.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipe(String key, HarvestDropRecipeManager.HarvestTargetMatcher targetMatcher,
                                         ItemStack outputStack, boolean preventDuplicateStacking,
                                         float chance, float fortuneBonus,
                                         List<HarvestDropRecipeManager.HarvestDropDisplayBlockState> displayBlockStates,
                                         IBlockState displaySupportBlockState) {
        return HarvestDropRecipeManager.registerRecipe(key, targetMatcher, outputStack, preventDuplicateStacking,
                chance, fortuneBonus, displayBlockStates, displaySupportBlockState);
    }

    /**
     * Registers a recipe that is shown only in JEI.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputStack The output stack to register, remove, match, or display.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param chance The base chance for this output or effect, using the 0.0 to 1.0 range.
     * @param fortuneBonus The additional drop chance added per Fortune level.
     * @param displayBlockState The single block state rendered in the harvest drop JEI preview.
     * @param displaySupportBlockState The optional support block rendered below each occupied preview column; {@code null} renders no support block.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipeJei(String key, HarvestDropRecipeManager.HarvestTargetMatcher targetMatcher,
                                            ItemStack outputStack, boolean preventDuplicateStacking,
                                            float chance, float fortuneBonus, IBlockState displayBlockState,
                                            IBlockState displaySupportBlockState) {
        return HarvestDropRecipeManager.registerRecipeJei(key, targetMatcher, outputStack, preventDuplicateStacking,
                chance, fortuneBonus, displayBlockState, displaySupportBlockState);
    }

    /**
     * Registers a recipe that is shown only in JEI.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputStack The output stack to register, remove, match, or display.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param chance The base chance for this output or effect, using the 0.0 to 1.0 range.
     * @param fortuneBonus The additional drop chance added per Fortune level.
     * @param displayBlockStates The block states rendered in the harvest drop JEI preview, including relative offsets for multi-block previews.
     * @param displaySupportBlockState The optional support block rendered below each occupied preview column; {@code null} renders no support block.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipeJei(String key, HarvestDropRecipeManager.HarvestTargetMatcher targetMatcher,
                                            ItemStack outputStack, boolean preventDuplicateStacking,
                                            float chance, float fortuneBonus,
                                            List<HarvestDropRecipeManager.HarvestDropDisplayBlockState> displayBlockStates,
                                            IBlockState displaySupportBlockState) {
        return HarvestDropRecipeManager.registerRecipeJei(key, targetMatcher, outputStack, preventDuplicateStacking,
                chance, fortuneBonus, displayBlockStates, displaySupportBlockState);
    }

    /**
     * Registers a runtime recipe.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputs The output entries registered for this drop recipe.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param displayBlockState The single block state rendered in the harvest drop JEI preview.
     * @param displaySupportBlockState The optional support block rendered below each occupied preview column; {@code null} renders no support block.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipe(String key, HarvestDropRecipeManager.HarvestTargetMatcher targetMatcher,
                                         List<HuntingDropOutput> outputs, boolean preventDuplicateStacking,
                                         IBlockState displayBlockState, IBlockState displaySupportBlockState) {
        return HarvestDropRecipeManager.registerRecipe(key, targetMatcher, outputs, preventDuplicateStacking,
                displayBlockState, displaySupportBlockState);
    }

    /**
     * Registers a runtime recipe.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputs The output entries registered for this drop recipe.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param displayBlockStates The block states rendered in the harvest drop JEI preview, including relative offsets for multi-block previews.
     * @param displaySupportBlockState The optional support block rendered below each occupied preview column; {@code null} renders no support block.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipe(String key, HarvestDropRecipeManager.HarvestTargetMatcher targetMatcher,
                                         List<HuntingDropOutput> outputs, boolean preventDuplicateStacking,
                                         List<HarvestDropRecipeManager.HarvestDropDisplayBlockState> displayBlockStates,
                                         IBlockState displaySupportBlockState) {
        return HarvestDropRecipeManager.registerRecipe(key, targetMatcher, outputs, preventDuplicateStacking,
                displayBlockStates, displaySupportBlockState);
    }

    /**
     * Registers a recipe that is shown only in JEI.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputs The output entries registered for this drop recipe.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param displayBlockState The single block state rendered in the harvest drop JEI preview.
     * @param displaySupportBlockState The optional support block rendered below each occupied preview column; {@code null} renders no support block.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipeJei(String key, HarvestDropRecipeManager.HarvestTargetMatcher targetMatcher,
                                            List<HuntingDropOutput> outputs, boolean preventDuplicateStacking,
                                            IBlockState displayBlockState, IBlockState displaySupportBlockState) {
        return HarvestDropRecipeManager.registerRecipeJei(key, targetMatcher, outputs, preventDuplicateStacking,
                displayBlockState, displaySupportBlockState);
    }

    /**
     * Registers a recipe that is shown only in JEI.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputs The output entries registered for this drop recipe.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param displayBlockStates The block states rendered in the harvest drop JEI preview, including relative offsets for multi-block previews.
     * @param displaySupportBlockState The optional support block rendered below each occupied preview column; {@code null} renders no support block.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipeJei(String key, HarvestDropRecipeManager.HarvestTargetMatcher targetMatcher,
                                            List<HuntingDropOutput> outputs, boolean preventDuplicateStacking,
                                            List<HarvestDropRecipeManager.HarvestDropDisplayBlockState> displayBlockStates,
                                            IBlockState displaySupportBlockState) {
        return HarvestDropRecipeManager.registerRecipeJei(key, targetMatcher, outputs, preventDuplicateStacking,
                displayBlockStates, displaySupportBlockState);
    }

    /**
     * Creates one harvest-drop preview block state entry.
     *
     * @param blockState The block state to render in the harvest drop JEI preview.
     * @param offsetX The preview X offset relative to the recipe origin.
     * @param offsetY The preview Y offset relative to the recipe origin.
     * @param offsetZ The preview Z offset relative to the recipe origin.
     * @return The result produced by this API method.
     */
    public static HarvestDropRecipeManager.HarvestDropDisplayBlockState createHarvestDropDisplayBlockState(
            IBlockState blockState, int offsetX, int offsetY, int offsetZ) {
        return HarvestDropRecipeManager.createHarvestDropDisplayBlockState(blockState, offsetX, offsetY, offsetZ);
    }

    /**
     * Unregisters a recipe by key.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @return The result produced by this API method.
     */
    public static boolean unregisterRecipe(String key) {
        return HarvestDropRecipeManager.unregisterRecipe(key);
    }

    /**
     * Returns the registered recipe views.
     *
     * @return The result produced by this API method.
     */
    public static List<HarvestDropRecipeManager.HarvestDropRecipeView> getRecipes() {
        return HarvestDropRecipeManager.getRecipes();
    }

    /**
     * Evaluates a target matcher safely.
     *
     * @param state The block state being evaluated.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @return The result produced by this API method.
     */
    public static boolean matches(IBlockState state, HarvestDropRecipeManager.HarvestTargetMatcher targetMatcher) {
        return targetMatcher != null && targetMatcher.matches(state);
    }
}
