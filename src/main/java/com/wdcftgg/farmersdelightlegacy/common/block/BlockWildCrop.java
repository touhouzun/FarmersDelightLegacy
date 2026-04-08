package com.wdcftgg.farmersdelightlegacy.common.block;

import net.minecraft.block.Block;
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

public class BlockWildCrop extends BlockBush implements IGrowable {
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.8125D, 0.875D);

    public BlockWildCrop() {
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
        Block block = state.getBlock();
        return super.canSustainBush(state) || block == net.minecraft.init.Blocks.GRASS || block == net.minecraft.init.Blocks.DIRT || block == net.minecraft.init.Blocks.SAND;
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return rand.nextFloat() < 0.8F;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        int nearbyCount = 0;
        for (BlockPos checkPos : BlockPos.getAllInBoxMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
            if (worldIn.getBlockState(checkPos).getBlock() == this) {
                nearbyCount++;
                if (nearbyCount >= 10) {
                    return;
                }
            }
        }

        BlockPos target = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
        for (int i = 0; i < 4; i++) {
            if (worldIn.isAirBlock(target) && this.canPlaceBlockAt(worldIn, target)) {
                pos = target;
            }
            target = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
        }

        if (worldIn.isAirBlock(target) && this.canPlaceBlockAt(worldIn, target)) {
            worldIn.setBlockState(target, this.getDefaultState(), 2);
        }
    }
}

