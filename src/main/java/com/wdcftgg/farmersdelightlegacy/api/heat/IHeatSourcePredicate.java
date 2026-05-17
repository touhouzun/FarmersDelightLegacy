package com.wdcftgg.farmersdelightlegacy.api.heat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Callback interface for direct heat source checks.
 * <p>
 * Register implementations with {@link HeatSourceApi#registerDirectHeatSourcePredicate(String, IHeatSourcePredicate)}.
 */
@FunctionalInterface
public interface IHeatSourcePredicate {
    /**
     * Checks whether a block state should count as a direct heat source.
     *
     * @param world The world where the predicate is evaluated.
     * @param pos   The block position being evaluated.
     * @param state The block state being evaluated.
     * @return {@code true} when the block should act as a direct heat source.
     */
    boolean isHeatSource(World world, BlockPos pos, IBlockState state);
}
