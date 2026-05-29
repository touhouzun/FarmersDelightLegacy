package com.wdcftgg.farmersdelightlegacy.common.compat;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

@Mod.EventBusSubscriber(modid = FarmersDelightLegacy.MOD_ID)
public final class NyxSandyShrubBonemealCompat {
    public static final String NyxModId = "nyx";

    private NyxSandyShrubBonemealCompat() {
    }

    @SubscribeEvent
    public static void onBonemealSandyShrub(BonemealEvent event) {
        if (!Loader.isModLoaded(NyxModId) || event.getBlock().getBlock() != ModBlocks.SANDY_SHRUB) {
            return;
        }

        World world = event.getWorld();
        if (!world.isRemote) {
            spreadSandyShrub(world, world.rand, event.getPos());
        }

        event.setResult(Event.Result.ALLOW);
    }

    private static void spreadSandyShrub(World world, Random random, BlockPos sourcePos) {
        Block sandyShrubBlock = ModBlocks.SANDY_SHRUB;
        IBlockState sandyShrubState = sandyShrubBlock.getDefaultState();
        for (int attemptIndex = 0; attemptIndex < 6; attemptIndex++) {
            BlockPos targetPos = sourcePos.add(
                    random.nextInt(2 * 2 + 1) - 2,
                    0,
                    random.nextInt(2 * 2 + 1) - 2);
            if (world.isAirBlock(targetPos) && sandyShrubBlock.canPlaceBlockAt(world, targetPos)) {
                world.setBlockState(targetPos, sandyShrubState, 2);
            }
        }
    }
}
