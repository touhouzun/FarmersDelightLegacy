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

    /**
     * Registers an organic compost base condition.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param condition The decomposition base condition callback to register.
     */
    public static void registerBaseCondition(String key, DecompositionRecipeManager.DecompositionCondition condition) {
        DecompositionRecipeManager.registerBaseCondition(key, condition);
    }

    /**
     * Unregisters an organic compost base condition.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     */
    public static void unregisterBaseCondition(String key) {
        DecompositionRecipeManager.unregisterBaseCondition(key);
    }

    /**
     * Registers an organic compost accelerator.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param accelerator The decomposition accelerator callback to register.
     * @param displayStack The stack shown in JEI or other displays for this accelerator.
     */
    public static void registerAccelerator(String key, DecompositionRecipeManager.DecompositionAccelerator accelerator, ItemStack displayStack) {
        DecompositionRecipeManager.registerAccelerator(key, accelerator, displayStack);
    }

    /**
     * Unregisters an organic compost accelerator.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     */
    public static void unregisterAccelerator(String key) {
        DecompositionRecipeManager.unregisterAccelerator(key);
    }

    /**
     * Calculates the decomposition chance for a compost block.
     *
     * @param world The world where the predicate or query is evaluated.
     * @param compostPos The organic compost block position being evaluated.
     * @return The result produced by this API method.
     */
    public static float getDecompositionChance(World world, BlockPos compostPos) {
        return DecompositionRecipeManager.getDecompositionChance(world, compostPos);
    }

    /**
     * Returns the display stacks for registered accelerators.
     *
     * @return The result produced by this API method.
     */
    public static List<ItemStack> getAcceleratorDisplayStacks() {
        return DecompositionRecipeManager.getAcceleratorDisplayStacks();
    }
}
