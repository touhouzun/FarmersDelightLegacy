package com.wdcftgg.farmersdelightlegacy.common.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockShepherdsPieFeast extends BlockFeast {

    private static final AxisAlignedBB PLATE_SHAPE = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.125D, 0.9375D);
    private static final AxisAlignedBB PIE_SHAPE = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.5D, 0.9375D);

    public BlockShepherdsPieFeast(int maxServings, String servingItemPath, String requiredContainerPath, boolean hasLeftovers) {
        super(maxServings, servingItemPath, requiredContainerPath, hasLeftovers);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(getServingsProperty()) == 0 ? PLATE_SHAPE : PIE_SHAPE;
    }
}
