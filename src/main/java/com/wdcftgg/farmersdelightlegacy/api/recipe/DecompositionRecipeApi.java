package com.wdcftgg.farmersdelightlegacy.api.recipe;

import com.wdcftgg.farmersdelightlegacy.common.recipe.DecompositionRecipeManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Organic Compost decomposition API.
 * <p>
 * Base conditions run once per decomposition tick. Accelerators run for each nearby scanned block.
 */
public final class DecompositionRecipeApi {

    private DecompositionRecipeApi() {
    }

    public static void registerBaseCondition(String key, DecompositionRecipeManager.DecompositionCondition condition) {
        DecompositionRecipeManager.registerBaseCondition(key, condition);
    }

    public static void unregisterBaseCondition(String key) {
        DecompositionRecipeManager.unregisterBaseCondition(key);
    }

    public static void registerAccelerator(String key, DecompositionRecipeManager.DecompositionAccelerator accelerator, ItemStack displayStack) {
        DecompositionRecipeManager.registerAccelerator(key, accelerator, displayStack);
    }

    public static void unregisterAccelerator(String key) {
        DecompositionRecipeManager.unregisterAccelerator(key);
    }

    public static float getDecompositionChance(World world, BlockPos compostPos) {
        return DecompositionRecipeManager.getDecompositionChance(world, compostPos);
    }

    public static List<ItemStack> getAcceleratorDisplayStacks() {
        return DecompositionRecipeManager.getAcceleratorDisplayStacks();
    }
}