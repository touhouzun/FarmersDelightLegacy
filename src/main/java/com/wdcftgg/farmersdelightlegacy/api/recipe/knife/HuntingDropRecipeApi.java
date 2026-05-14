package com.wdcftgg.farmersdelightlegacy.api.recipe.knife;

import com.wdcftgg.farmersdelightlegacy.common.recipe.HuntingDropRecipeManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * Hunting drop registration API.
 * <p>
 * Recipes run when a living entity is killed by a knife. Use burningRequired to select normal or burning targets.
 * Each HuntingDropOutput controls its own chance and Looting bonus.
 */
public final class HuntingDropRecipeApi {

    private HuntingDropRecipeApi() {
    }

    public static boolean registerRecipe(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher, ItemStack outputStack) {
        return HuntingDropRecipeManager.registerRecipe(key, targetMatcher, outputStack);
    }

    public static boolean registerRecipe(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher,
                                         ItemStack outputStack, boolean preventDuplicateStacking) {
        return HuntingDropRecipeManager.registerRecipe(key, targetMatcher, outputStack, preventDuplicateStacking);
    }

    public static boolean registerRecipe(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher,
                                         ItemStack outputStack, boolean preventDuplicateStacking,
                                         float chance, float lootingBonus, boolean burningRequired,
                                         ResourceLocation entityId) {
        return HuntingDropRecipeManager.registerRecipe(key, targetMatcher, outputStack, preventDuplicateStacking,
                chance, lootingBonus, burningRequired, entityId);
    }

    public static boolean registerRecipe(String key, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher,
                                         List<HuntingDropOutput> outputs, boolean burningRequired,
                                         boolean preventDuplicateStacking, ResourceLocation entityId) {
        return HuntingDropRecipeManager.registerRecipe(key, targetMatcher, outputs, burningRequired,
                preventDuplicateStacking, entityId);
    }

    public static boolean unregisterRecipe(String key) {
        return HuntingDropRecipeManager.unregisterRecipe(key);
    }

    public static List<HuntingDropRecipeManager.HuntingDropRecipeView> getRecipes() {
        return HuntingDropRecipeManager.getRecipes();
    }

    public static boolean matches(EntityLivingBase target, HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher) {
        return targetMatcher != null && targetMatcher.matches(target);
    }
}