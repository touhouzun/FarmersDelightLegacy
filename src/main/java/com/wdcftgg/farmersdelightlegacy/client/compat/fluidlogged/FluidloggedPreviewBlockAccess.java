package com.wdcftgg.farmersdelightlegacy.client.compat.fluidlogged;

import com.wdcftgg.farmersdelightlegacy.client.jei.HarvestDropJeiRecipe;
import com.wdcftgg.farmersdelightlegacy.common.recipe.HarvestDropRecipeManager;
import git.jbredwards.fluidlogged_api.api.block.IFluidloggable;
import git.jbredwards.fluidlogged_api.api.util.FluidState;
import git.jbredwards.fluidlogged_api.api.world.IFluidStateProvider;
import git.jbredwards.fluidlogged_api.api.world.IWorldProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FluidloggedPreviewBlockAccess extends HarvestDropJeiRecipe.PreviewBlockAccess
        implements IFluidStateProvider, IWorldProvider {
    private final Map<BlockPos, FluidState> fluidStates = new HashMap<>();

    public FluidloggedPreviewBlockAccess(
            List<HarvestDropRecipeManager.HarvestDropDisplayBlockState> displayBlockStates) {
        super(displayBlockStates);
        collectFluidStates(displayBlockStates);
    }

    @Override
    public FluidState getFluidState(int blockPositionX, int blockPositionY, int blockPositionZ) {
        FluidState fluidState = this.fluidStates.get(new BlockPos(blockPositionX, blockPositionY, blockPositionZ));
        return fluidState == null ? FluidState.EMPTY : fluidState;
    }

    @Override
    public World getWorld() {
        return Minecraft.getMinecraft().world;
    }

    @Override
    public IBlockState getContainedFluidBlockState(BlockPos pos) {
        FluidState fluidState = getFluidState(pos.getX(), pos.getY(), pos.getZ());
        return fluidState.isEmpty() ? null : fluidState.getState();
    }

    private void collectFluidStates(
            List<HarvestDropRecipeManager.HarvestDropDisplayBlockState> displayBlockStates) {
        for (HarvestDropRecipeManager.HarvestDropDisplayBlockState displayBlockState : displayBlockStates) {
            BlockPos blockPosition = new BlockPos(displayBlockState.getOffsetX(), displayBlockState.getOffsetY(),
                    displayBlockState.getOffsetZ());
            addWaterFluidStateIfFluidloggable(blockPosition, displayBlockState.getBlockState());
        }
    }

    private void addWaterFluidStateIfFluidloggable(BlockPos blockPosition, IBlockState blockState) {
        if (!(blockState.getBlock() instanceof IFluidloggable)) {
            return;
        }
        IFluidloggable fluidloggableBlock = (IFluidloggable) blockState.getBlock();
        FluidState waterFluidState = FluidState.of(FluidRegistry.WATER);
        this.fluidStates.put(blockPosition, waterFluidState);
        if (!fluidloggableBlock.isFluidloggable(blockState, this, blockPosition, waterFluidState)
                || !fluidloggableBlock.shouldFluidRender(this, blockPosition, blockState, waterFluidState)) {
            this.fluidStates.remove(blockPosition);
        }
    }
}
