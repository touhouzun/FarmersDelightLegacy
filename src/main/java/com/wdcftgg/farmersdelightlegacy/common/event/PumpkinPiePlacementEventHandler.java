package com.wdcftgg.farmersdelightlegacy.common.event;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.common.Configuration;
import com.wdcftgg.farmersdelightlegacy.common.block.BlockPie;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = FarmersDelightLegacy.MOD_ID)
public final class PumpkinPiePlacementEventHandler {

    private PumpkinPiePlacementEventHandler() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != EnumHand.MAIN_HAND) {
            return;
        }

        EntityPlayer player = event.getEntityPlayer();
        if (player == null || Configuration.enablePumpkinPieSneakToPlace && !player.isSneaking()) {
            return;
        }

        ItemStack heldStack = player.getHeldItem(event.getHand());
        if (heldStack.isEmpty() || heldStack.getItem() != Items.PUMPKIN_PIE) {
            return;
        }

        World world = event.getWorld();
        BlockPos placePos = getPumpkinPiePlacePos(world, event.getPos(), event.getFace());
        if (placePos == null || !player.canPlayerEdit(placePos, event.getFace(), heldStack)) {
            return;
        }

        IBlockState placedState = ModBlocks.pumpkinPie.getDefaultState()
                .withProperty(BlockPie.FACING, player.getHorizontalFacing());
        if (!world.mayPlace(ModBlocks.pumpkinPie, placePos, false, event.getFace(), player)
                || !ModBlocks.pumpkinPie.canPlaceBlockAt(world, placePos)) {
            return;
        }

        if (!world.isRemote) {
            world.setBlockState(placePos, placedState, 11);
            SoundType soundType = placedState.getBlock().getSoundType(placedState, world, placePos, player);
            world.playSound(null, placePos, soundType.getPlaceSound(), SoundCategory.BLOCKS,
                    (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
            if (!player.capabilities.isCreativeMode) {
                heldStack.shrink(1);
            }
        }

        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }

    private static BlockPos getPumpkinPiePlacePos(World world, BlockPos clickedPos, EnumFacing clickedFace) {
        if (clickedFace == null) {
            return null;
        }
        IBlockState clickedState = world.getBlockState(clickedPos);
        Block clickedBlock = clickedState.getBlock();
        return clickedBlock.isReplaceable(world, clickedPos) ? clickedPos : clickedPos.offset(clickedFace);
    }
}
