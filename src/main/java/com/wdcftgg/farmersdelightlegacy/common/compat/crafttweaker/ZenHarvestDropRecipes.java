package com.wdcftgg.farmersdelightlegacy.common.compat.crafttweaker;

import com.wdcftgg.farmersdelightlegacy.api.recipe.knife.HuntingDropOutput;
import com.wdcftgg.farmersdelightlegacy.common.recipe.manager.HarvestDropRecipeManager;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

@ZenRegister
@ZenClass("mods.farmersdelight.HarvestDrop")
public final class ZenHarvestDropRecipes {

    private ZenHarvestDropRecipes() {
    }

    @ZenMethod
    public static boolean addRecipe(String key, IBlockState targetBlockState, IItemStack outputStack) {
        return addRecipeAdvanced(key, targetBlockState, new IItemStack[]{outputStack}, null, null, false);
    }

    @ZenMethod
    public static boolean addRecipeAdvanced(String key, IBlockState targetBlockState, IItemStack[] outputStacks,
                                            float[] chances, float[] fortuneBonuses, boolean preventDuplicateStacking) {
        return registerRecipe(key, targetBlockState, outputStacks, chances, fortuneBonuses,
                preventDuplicateStacking, false);
    }

    @ZenMethod
    public static boolean addJeiRecipe(String key, IBlockState targetBlockState, IItemStack outputStack) {
        return addJeiRecipeAdvanced(key, targetBlockState, new IItemStack[]{outputStack}, null, null, false);
    }

    @ZenMethod
    public static boolean addJeiRecipeAdvanced(String key, IBlockState targetBlockState, IItemStack[] outputStacks,
                                               float[] chances, float[] fortuneBonuses, boolean preventDuplicateStacking) {
        return registerRecipe(key, targetBlockState, outputStacks, chances, fortuneBonuses,
                preventDuplicateStacking, true);
    }

    @ZenMethod
    public static boolean removeRecipe(String key) {
        return HarvestDropRecipeManager.unregisterRecipe(key);
    }

    private static boolean registerRecipe(String key, IBlockState targetBlockState, IItemStack[] outputStacks,
                                          float[] chances, float[] fortuneBonuses,
                                          boolean preventDuplicateStacking, boolean jeiOnly) {
        net.minecraft.block.state.IBlockState nativeTargetState = CraftTweakerMC.getBlockState(targetBlockState);
        List<HuntingDropOutput> outputs = CraftTweakerCompatHelper.toDropOutputs(outputStacks, chances, fortuneBonuses);
        if (nativeTargetState == null || outputs == null) {
            return false;
        }

        HarvestDropRecipeManager.HarvestTargetMatcher targetMatcher = state -> nativeTargetState.equals(state);
        if (jeiOnly) {
            return HarvestDropRecipeManager.registerRecipeJei(key, targetMatcher, outputs, preventDuplicateStacking,
                    nativeTargetState, null);
        }
        return HarvestDropRecipeManager.registerRecipe(key, targetMatcher, outputs, preventDuplicateStacking,
                nativeTargetState, null);
    }
}
