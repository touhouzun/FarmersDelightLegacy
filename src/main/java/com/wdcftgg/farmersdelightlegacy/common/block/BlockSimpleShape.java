package com.wdcftgg.farmersdelightlegacy.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockSimpleShape extends Block {
    private final AxisAlignedBB shape;
    private final boolean collisionEnabled;

    public BlockSimpleShape(Material material, SoundType soundType, float hardness, AxisAlignedBB shape, boolean collisionEnabled) {
        super(material);
        this.shape = shape;
        this.collisionEnabled = collisionEnabled;
        this.setHardness(hardness);
        this.setResistance(hardness + 2.0F);
        this.setSoundType(soundType);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return shape;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return collisionEnabled ? shape : NULL_AABB;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return !collisionEnabled;
    }
}

