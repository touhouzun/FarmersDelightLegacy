package com.wdcftgg.farmersdelightlegacy.common.block;

import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockSandyShrub extends BlockBush implements IGrowable {
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.8125D, 0.875D);

    public BlockSandyShrub() {
        super(Material.PLANTS);
        this.setHardness(0.0F);
        this.setSoundType(SoundType.PLANT);
        this.setTickRandomly(true);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        return state.getBlock() == net.minecraft.init.Blocks.SAND;
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        for (int i = 0; i < 6; i++) {
            BlockPos target = pos.add(rand.nextInt(5) - 2, 0, rand.nextInt(5) - 2);
            if (worldIn.isAirBlock(target) && this.canPlaceBlockAt(worldIn, target)) {
                worldIn.setBlockState(target, this.getDefaultState(), 2);
            }
        }
    }
}

