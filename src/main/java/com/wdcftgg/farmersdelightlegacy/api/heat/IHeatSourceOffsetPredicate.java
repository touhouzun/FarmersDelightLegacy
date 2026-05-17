package com.wdcftgg.farmersdelightlegacy.api.heat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Callback used to decide whether heat-source lookup should continue one block lower.
 */
@FunctionalInterface
public interface IHeatSourceOffsetPredicate {

    /**
     * Checks whether the block at the queried position should offset heat lookup downward.
     *
     * @param world The world where the predicate is evaluated.
     * @param pos The block position being evaluated.
     * @param state The block state being evaluated.
     * @return {@code true} when lookup should continue at the block below; otherwise {@code false}.
     */
    boolean shouldOffsetDown(World world, BlockPos pos, IBlockState state);
}
