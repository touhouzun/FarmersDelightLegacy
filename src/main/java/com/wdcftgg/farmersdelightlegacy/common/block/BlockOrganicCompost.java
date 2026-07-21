package com.wdcftgg.farmersdelightlegacy.common.block;

import com.wdcftgg.farmersdelightlegacy.common.recipe.manager.DecompositionRecipeManager;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockOrganicCompost extends Block {
    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 7);

    public BlockOrganicCompost() {
        super(Material.GROUND);
        this.setHardness(1.2F);
        this.setSoundType(SoundType.PLANT);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0));
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.isRemote) {
            return;
        }

        float chance = DecompositionRecipeManager.getDecompositionChance(worldIn, pos);
        if (rand.nextFloat() > chance) {
            return;
        }

        int level = state.getValue(LEVEL);
        if (level >= getMaxCompostingStage()) {
            worldIn.setBlockState(pos, ModBlocks.RICH_SOIL.getDefaultState(), 3);
        } else {
            worldIn.setBlockState(pos, state.withProperty(LEVEL, level + 1), 3);
        }
    }

    public int getMaxCompostingStage() {
        return 7;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return getMaxCompostingStage() + 1 - blockState.getValue(LEVEL);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.randomDisplayTick(stateIn, worldIn, pos, rand);
        if (rand.nextInt(10) == 0) {
            worldIn.spawnParticle(EnumParticleTypes.TOWN_AURA,
                    pos.getX() + rand.nextFloat(), pos.getY() + 1.1D, pos.getZ() + rand.nextFloat(),
                    0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(LEVEL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LEVEL, Math.max(0, Math.min(7, meta)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LEVEL);
    }
}
