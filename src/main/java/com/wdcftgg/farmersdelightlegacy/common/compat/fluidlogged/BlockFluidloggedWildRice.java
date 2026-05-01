package com.wdcftgg.farmersdelightlegacy.common.compat.fluidlogged;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockWildRice;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import git.jbredwards.fluidlogged_api.api.block.IFluidloggable;
import git.jbredwards.fluidlogged_api.api.util.FluidState;
import git.jbredwards.fluidlogged_api.api.util.FluidloggedUtils;
import git.jbredwards.fluidlogged_api.api.world.IWorldProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class BlockFluidloggedWildRice extends BlockWildRice implements IFluidloggable {

    public BlockFluidloggedWildRice() {
        super();
    }

    public static boolean placeInWorld(World world, BlockPos pos) {
        Block block = ModBlocks.WILD_RICE;
        if (!(block instanceof BlockFluidloggedWildRice)) {
            return false;
        }

        BlockFluidloggedWildRice wildRice = (BlockFluidloggedWildRice) block;
        if (!wildRice.canPlaceBlockAt(world, pos)) {
            return false;
        }

        IBlockState lowerState = wildRice.getDefaultState().withProperty(BlockWildRice.HALF, BlockDoublePlant.EnumBlockHalf.LOWER);
        IBlockState upperState = wildRice.getDefaultState().withProperty(BlockWildRice.HALF, BlockDoublePlant.EnumBlockHalf.UPPER);
        world.setBlockState(pos, lowerState, 2);
        world.setBlockState(pos.up(), upperState, 2);

        IBlockState placedState = world.getBlockState(pos);
        if (!FluidloggedUtils.setFluidState(world, pos, placedState, FluidState.of(FluidRegistry.WATER), true)) {
            world.setBlockToAir(pos.up());
            world.setBlockToAir(pos);
            return false;
        }
        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return this.canSustainBush(worldIn.getBlockState(pos.down()))
                && worldIn.isAirBlock(pos.up())
                && this.hasWaterState(worldIn, pos);
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        BlockDoublePlant.EnumBlockHalf half = state.getValue(BlockWildRice.HALF);
        if (half == BlockDoublePlant.EnumBlockHalf.UPPER) {
            IBlockState belowState = worldIn.getBlockState(pos.down());
            return belowState.getBlock() == this && belowState.getValue(BlockWildRice.HALF) == BlockDoublePlant.EnumBlockHalf.LOWER;
        }

        IBlockState soilState = worldIn.getBlockState(pos.down());
        return this.canSustainBush(soilState)
                && this.hasContainedWater(worldIn, pos, state)
                && worldIn.getBlockState(pos.up()).getBlock() == this;
    }

    @Override
    public boolean isFluidloggable(IBlockState state, World world, BlockPos pos) {
        return state.getValue(BlockWildRice.HALF) == BlockDoublePlant.EnumBlockHalf.LOWER
                && this.canSustainBush(world.getBlockState(pos.down()));
    }

    @Override
    public boolean isFluidValid(IBlockState state, World world, BlockPos pos, Fluid fluid) {
        return state.getValue(BlockWildRice.HALF) == BlockDoublePlant.EnumBlockHalf.LOWER
                && FluidloggedUtils.isCompatibleFluid(FluidRegistry.WATER, fluid);
    }

    @Override
    public boolean isFluidloggable(IBlockState state, IBlockAccess world, BlockPos pos, FluidState fluidState) {
        World actualWorld = IWorldProvider.getWorld(world);
        if (fluidState.isEmpty()) {
            return actualWorld != null && this.isFluidloggable(state, actualWorld, pos);
        }
        return state.getValue(BlockWildRice.HALF) == BlockDoublePlant.EnumBlockHalf.LOWER
                && fluidState.isSource()
                && actualWorld != null
                && this.isFluidValid(state, actualWorld, pos, fluidState.getFluid());
    }

    @Override
    public boolean canFluidFlow(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing facing) {
        return state.getValue(BlockWildRice.HALF) == BlockDoublePlant.EnumBlockHalf.LOWER;
    }

    @Override
    public boolean canFluidConnect(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing facing) {
        return state.getValue(BlockWildRice.HALF) == BlockDoublePlant.EnumBlockHalf.LOWER;
    }

    @Override
    public boolean shouldFluidRender(IBlockAccess world, BlockPos pos, IBlockState state, FluidState fluidState) {
        return state.getValue(BlockWildRice.HALF) == BlockDoublePlant.EnumBlockHalf.LOWER && !fluidState.isEmpty();
    }

    @Override
    public EnumActionResult onFluidChange(World world, BlockPos pos, IBlockState state, FluidState fluidState, int flags) {
        if (state.getValue(BlockWildRice.HALF) == BlockDoublePlant.EnumBlockHalf.LOWER
                && (fluidState.isEmpty() || !FluidloggedUtils.isCompatibleFluid(FluidRegistry.WATER, fluidState.getFluid()))) {
            this.removeWildRice(world, pos);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    @Override
    public EnumActionResult onFluidFill(World world, BlockPos pos, IBlockState state, FluidState fluidState, int flags) {
        return EnumActionResult.PASS;
    }

    @Override
    public EnumActionResult onFluidDrain(World world, BlockPos pos, IBlockState state, int flags) {
        if (state.getValue(BlockWildRice.HALF) == BlockDoublePlant.EnumBlockHalf.LOWER) {
            this.removeWildRice(world, pos);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    @Override
    public boolean overrideApplyDefaultsSetting() {
        return true;
    }

    private boolean hasWaterState(World worldIn, BlockPos pos) {
        FluidState fluidState = FluidloggedUtils.getFluidState(worldIn, pos);
        return !fluidState.isEmpty() && FluidloggedUtils.isCompatibleFluid(FluidRegistry.WATER, fluidState.getFluid()) && fluidState.isSource();
    }

    private boolean hasContainedWater(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
        FluidState fluidState = FluidloggedUtils.getFluidState(worldIn, pos, state);
        return !fluidState.isEmpty() && FluidloggedUtils.isCompatibleFluid(FluidRegistry.WATER, fluidState.getFluid()) && fluidState.isSource();
    }

    private void removeWildRice(World worldIn, BlockPos pos) {
        worldIn.destroyBlock(pos, true);
        if (worldIn.getBlockState(pos.up()).getBlock() == this) {
            worldIn.setBlockToAir(pos.up());
        }
        if (worldIn.getBlockState(pos.down()).getBlock() == this) {
            worldIn.setBlockToAir(pos.down());
        }
    }
}
