package com.wdcftgg.farmersdelightlegacy.api.recipe.knife;

import com.wdcftgg.farmersdelightlegacy.common.recipe.manager.HuntingDropRecipeManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;

/**
 * Hunting drop registration API.
 * <p>
 * Recipes run when a living entity is killed by a knife. Use burningRequired to select normal or burning targets.
 * Each HuntingDropOutput controls its own chance and Looting bonus.
 */
public final class HuntingDropRecipeApi {

    private HuntingDropRecipeApi() {
    }

    /**
     * Registers a runtime recipe.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputStack The output stack to register, remove, match, or display.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipe(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher, ItemStack outputStack) {
        return HuntingDropRecipeManager.registerRecipe(key, targetMatcher, outputStack);
    }

    /**
     * Registers a recipe that is shown only in JEI.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputStack The output stack to register, remove, match, or display.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipeJei(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher, ItemStack outputStack) {
        return HuntingDropRecipeManager.registerRecipeJei(key, targetMatcher, outputStack);
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
    public static boolean registerRecipe(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher,
                                         ItemStack outputStack, boolean preventDuplicateStacking) {
        return HuntingDropRecipeManager.registerRecipe(key, targetMatcher, outputStack, preventDuplicateStacking);
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
    public static boolean registerRecipeJei(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher,
                                            ItemStack outputStack, boolean preventDuplicateStacking) {
        return HuntingDropRecipeManager.registerRecipeJei(key, targetMatcher, outputStack, preventDuplicateStacking);
    }

    /**
     * Registers a runtime recipe.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputStack The output stack to register, remove, match, or display.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param chance The base chance for this output or effect, using the 0.0 to 1.0 range.
     * @param lootingBonus The additional drop chance added per Looting level.
     * @param burningRequired Whether the target must be burning for the recipe to match.
     * @param entityId The entity registry id rendered in JEI for this hunting drop recipe.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipe(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher,
                                         ItemStack outputStack, boolean preventDuplicateStacking,
                                         float chance, float lootingBonus, boolean burningRequired,
                                         ResourceLocation entityId) {
        return HuntingDropRecipeManager.registerRecipe(key, targetMatcher, outputStack, preventDuplicateStacking,
                chance, lootingBonus, burningRequired, entityId);
    }

    /**
     * Registers a recipe that is shown only in JEI.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputStack The output stack to register, remove, match, or display.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param chance The base chance for this output or effect, using the 0.0 to 1.0 range.
     * @param lootingBonus The additional drop chance added per Looting level.
     * @param burningRequired Whether the target must be burning for the recipe to match.
     * @param entityId The entity registry id rendered in JEI for this hunting drop recipe.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipeJei(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher,
                                            ItemStack outputStack, boolean preventDuplicateStacking,
                                            float chance, float lootingBonus, boolean burningRequired,
                                            ResourceLocation entityId) {
        return HuntingDropRecipeManager.registerRecipeJei(key, targetMatcher, outputStack, preventDuplicateStacking,
                chance, lootingBonus, burningRequired, entityId);
    }

    /**
     * Registers a runtime recipe.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputStack The output stack to register, remove, match, or display.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param chance The base chance for this output or effect, using the 0.0 to 1.0 range.
     * @param lootingBonus The additional drop chance added per Looting level.
     * @param entityId The entity registry id rendered in JEI for this hunting drop recipe.
     * @param jeiEntityConfigurator A client-side callback that mutates the JEI preview entity before rendering.
     * @param jeiDisplayTexts Text lines shown in the JEI recipe display and cycled by the clock control.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipe(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher,
                                         ItemStack outputStack, boolean preventDuplicateStacking,
                                         float chance, float lootingBonus, ResourceLocation entityId,
                                         Consumer<EntityLivingBase> jeiEntityConfigurator,
                                         List<String> jeiDisplayTexts) {
        return HuntingDropRecipeManager.registerRecipe(key, targetMatcher, outputStack, preventDuplicateStacking,
                chance, lootingBonus, entityId, jeiEntityConfigurator, jeiDisplayTexts);
    }

    /**
     * Registers a recipe that is shown only in JEI.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputStack The output stack to register, remove, match, or display.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param chance The base chance for this output or effect, using the 0.0 to 1.0 range.
     * @param lootingBonus The additional drop chance added per Looting level.
     * @param entityId The entity registry id rendered in JEI for this hunting drop recipe.
     * @param jeiEntityConfigurator A client-side callback that mutates the JEI preview entity before rendering.
     * @param jeiDisplayTexts Text lines shown in the JEI recipe display and cycled by the clock control.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipeJei(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher,
                                            ItemStack outputStack, boolean preventDuplicateStacking,
                                            float chance, float lootingBonus, ResourceLocation entityId,
                                            Consumer<EntityLivingBase> jeiEntityConfigurator,
                                            List<String> jeiDisplayTexts) {
        return HuntingDropRecipeManager.registerRecipeJei(key, targetMatcher, outputStack, preventDuplicateStacking,
                chance, lootingBonus, entityId, jeiEntityConfigurator, jeiDisplayTexts);
    }

    /**
     * Registers a runtime recipe.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputStack The output stack to register, remove, match, or display.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param chance The base chance for this output or effect, using the 0.0 to 1.0 range.
     * @param lootingBonus The additional drop chance added per Looting level.
     * @param entityId The entity registry id rendered in JEI for this hunting drop recipe.
     * @param jeiEntityConfigurator A client-side callback that mutates the JEI preview entity before rendering.
     * @param runJeiEntityConfiguratorEveryTick Whether {@code jeiEntityConfigurator} should run every JEI tick instead of only when the preview entity is created.
     * @param jeiDisplayTexts Text lines shown in the JEI recipe display and cycled by the clock control.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipe(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher,
                                         ItemStack outputStack, boolean preventDuplicateStacking,
                                         float chance, float lootingBonus, ResourceLocation entityId,
                                         Consumer<EntityLivingBase> jeiEntityConfigurator,
                                         boolean runJeiEntityConfiguratorEveryTick,
                                         List<String> jeiDisplayTexts) {
        return HuntingDropRecipeManager.registerRecipe(key, targetMatcher, outputStack, preventDuplicateStacking,
                chance, lootingBonus, entityId, jeiEntityConfigurator, runJeiEntityConfiguratorEveryTick,
                jeiDisplayTexts);
    }

    /**
     * Registers a recipe that is shown only in JEI.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputStack The output stack to register, remove, match, or display.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param chance The base chance for this output or effect, using the 0.0 to 1.0 range.
     * @param lootingBonus The additional drop chance added per Looting level.
     * @param entityId The entity registry id rendered in JEI for this hunting drop recipe.
     * @param jeiEntityConfigurator A client-side callback that mutates the JEI preview entity before rendering.
     * @param runJeiEntityConfiguratorEveryTick Whether {@code jeiEntityConfigurator} should run every JEI tick instead of only when the preview entity is created.
     * @param jeiDisplayTexts Text lines shown in the JEI recipe display and cycled by the clock control.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipeJei(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher,
                                            ItemStack outputStack, boolean preventDuplicateStacking,
                                            float chance, float lootingBonus, ResourceLocation entityId,
                                            Consumer<EntityLivingBase> jeiEntityConfigurator,
                                            boolean runJeiEntityConfiguratorEveryTick,
                                            List<String> jeiDisplayTexts) {
        return HuntingDropRecipeManager.registerRecipeJei(key, targetMatcher, outputStack, preventDuplicateStacking,
                chance, lootingBonus, entityId, jeiEntityConfigurator, runJeiEntityConfiguratorEveryTick,
                jeiDisplayTexts);
    }

    /**
     * Registers a runtime recipe.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputs The output entries registered for this drop recipe.
     * @param burningRequired Whether the target must be burning for the recipe to match.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param entityId The entity registry id rendered in JEI for this hunting drop recipe.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipe(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher,
                                         List<HuntingDropOutput> outputs, boolean burningRequired,
                                         boolean preventDuplicateStacking, ResourceLocation entityId) {
        return HuntingDropRecipeManager.registerRecipe(key, targetMatcher, outputs, burningRequired,
                preventDuplicateStacking, entityId);
    }

    /**
     * Registers a recipe that is shown only in JEI.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputs The output entries registered for this drop recipe.
     * @param burningRequired Whether the target must be burning for the recipe to match.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param entityId The entity registry id rendered in JEI for this hunting drop recipe.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipeJei(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher,
                                            List<HuntingDropOutput> outputs, boolean burningRequired,
                                            boolean preventDuplicateStacking, ResourceLocation entityId) {
        return HuntingDropRecipeManager.registerRecipeJei(key, targetMatcher, outputs, burningRequired,
                preventDuplicateStacking, entityId);
    }

    /**
     * Registers an advanced runtime hunting drop recipe.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputs The output entries registered for this drop recipe.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param entityId The entity registry id rendered in JEI for this hunting drop recipe.
     * @param jeiEntityConfigurator A client-side callback that mutates the JEI preview entity before rendering.
     * @param jeiDisplayTexts Text lines shown in the JEI recipe display and cycled by the clock control.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipeAdvance(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher,
                                                List<HuntingDropOutput> outputs, boolean preventDuplicateStacking,
                                                ResourceLocation entityId,
                                                Consumer<EntityLivingBase> jeiEntityConfigurator,
                                                List<String> jeiDisplayTexts) {
        return HuntingDropRecipeManager.registerRecipe(key, targetMatcher, outputs, preventDuplicateStacking,
                entityId, jeiEntityConfigurator, jeiDisplayTexts);
    }

    /**
     * Registers an advanced hunting drop recipe that is shown only in JEI.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputs The output entries registered for this drop recipe.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param entityId The entity registry id rendered in JEI for this hunting drop recipe.
     * @param jeiEntityConfigurator A client-side callback that mutates the JEI preview entity before rendering.
     * @param jeiDisplayTexts Text lines shown in the JEI recipe display and cycled by the clock control.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipeAdvanceJei(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher,
                                                   List<HuntingDropOutput> outputs, boolean preventDuplicateStacking,
                                                   ResourceLocation entityId,
                                                   Consumer<EntityLivingBase> jeiEntityConfigurator,
                                                   List<String> jeiDisplayTexts) {
        return HuntingDropRecipeManager.registerRecipeJei(key, targetMatcher, outputs, preventDuplicateStacking,
                entityId, jeiEntityConfigurator, jeiDisplayTexts);
    }

    /**
     * Registers an advanced runtime hunting drop recipe.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputs The output entries registered for this drop recipe.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param entityId The entity registry id rendered in JEI for this hunting drop recipe.
     * @param jeiEntityConfigurator A client-side callback that mutates the JEI preview entity before rendering.
     * @param runJeiEntityConfiguratorEveryTick Whether {@code jeiEntityConfigurator} should run every JEI tick instead of only when the preview entity is created.
     * @param jeiDisplayTexts Text lines shown in the JEI recipe display and cycled by the clock control.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipeAdvance(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher,
                                                List<HuntingDropOutput> outputs, boolean preventDuplicateStacking,
                                                ResourceLocation entityId,
                                                Consumer<EntityLivingBase> jeiEntityConfigurator,
                                                boolean runJeiEntityConfiguratorEveryTick,
                                                List<String> jeiDisplayTexts) {
        return HuntingDropRecipeManager.registerRecipe(key, targetMatcher, outputs, preventDuplicateStacking,
                entityId, jeiEntityConfigurator, runJeiEntityConfiguratorEveryTick, jeiDisplayTexts);
    }

    /**
     * Registers an advanced hunting drop recipe that is shown only in JEI.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @param outputs The output entries registered for this drop recipe.
     * @param preventDuplicateStacking Whether matching output stacks should be merged into an existing drop stack instead of adding a separate stack.
     * @param entityId The entity registry id rendered in JEI for this hunting drop recipe.
     * @param jeiEntityConfigurator A client-side callback that mutates the JEI preview entity before rendering.
     * @param runJeiEntityConfiguratorEveryTick Whether {@code jeiEntityConfigurator} should run every JEI tick instead of only when the preview entity is created.
     * @param jeiDisplayTexts Text lines shown in the JEI recipe display and cycled by the clock control.
     * @return The result produced by this API method.
     */
    public static boolean registerRecipeAdvanceJei(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher,
                                                   List<HuntingDropOutput> outputs, boolean preventDuplicateStacking,
                                                   ResourceLocation entityId,
                                                   Consumer<EntityLivingBase> jeiEntityConfigurator,
                                                   boolean runJeiEntityConfiguratorEveryTick,
                                                   List<String> jeiDisplayTexts) {
        return HuntingDropRecipeManager.registerRecipeJei(key, targetMatcher, outputs, preventDuplicateStacking,
                entityId, jeiEntityConfigurator, runJeiEntityConfiguratorEveryTick, jeiDisplayTexts);
    }

    /**
     * Unregisters a recipe by key.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @return The result produced by this API method.
     */
    public static boolean unregisterRecipe(String key) {
        return HuntingDropRecipeManager.unregisterRecipe(key);
    }

    /**
     * Returns the registered recipe views.
     *
     * @return The result produced by this API method.
     */
    public static List<HuntingDropRecipeManager.HuntingDropRecipeView> getRecipes() {
        return HuntingDropRecipeManager.getRecipes();
    }

    /**
     * Evaluates a target matcher safely.
     *
     * @param target The entity being tested against the hunting target matcher.
     * @param targetMatcher The predicate that decides whether the hunted entity or harvested block matches this recipe.
     * @return The result produced by this API method.
     */
    public static boolean matches(EntityLivingBase target, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher) {
        return targetMatcher != null && targetMatcher.matches(target);
    }
}
