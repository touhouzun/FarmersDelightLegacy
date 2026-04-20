package com.wdcftgg.farmersdelightlegacy.common.compat.crafttweaker;

import crafttweaker.api.block.IBlockState;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;

@FunctionalInterface
public interface ZenHeatSourcePredicateCallback {

    boolean test(IWorld world, IBlockPos pos, IBlockState iBlockState);
}

