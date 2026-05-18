package com.wdcftgg.farmersdelightlegacy.common.block.sign;

import com.wdcftgg.farmersdelightlegacy.common.network.ModNetworkHandler;
import com.wdcftgg.farmersdelightlegacy.common.network.PacketOpenCanvasSignEditor;
import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityCanvasSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

final class CanvasSignInteractionHelper {

    private CanvasSignInteractionHelper() {
    }

    static boolean openEditor(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        if (worldIn.isRemote) {
            return true;
        }

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileEntityCanvasSign && playerIn instanceof EntityPlayerMP) {
            ((TileEntityCanvasSign) tileEntity).setPlayer(playerIn);
            ModNetworkHandler.INSTANCE.sendTo(new PacketOpenCanvasSignEditor(pos), (EntityPlayerMP) playerIn);
        }
        return true;
    }

    @Mod.EventBusSubscriber
    static final class CanvasSignInteractionEvents {

        private CanvasSignInteractionEvents() {
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onRightClickCanvasSign(PlayerInteractEvent.RightClickBlock event) {
            if (event.getUseBlock() == Event.Result.DENY) {
                return;
            }

            World world = event.getWorld();
            BlockPos pos = event.getPos();
            IBlockState state = world.getBlockState(pos);
            if (!(state.getBlock() instanceof BlockCanvasStandingSign)
                    && !(state.getBlock() instanceof BlockCanvasWallSign)
                    && !(state.getBlock() instanceof BlockCanvasHangingSign)
                    && !(state.getBlock() instanceof BlockCanvasWallHangingSign)) {
                return;
            }

            TileEntity tileEntity = world.getTileEntity(pos);
            if (!(tileEntity instanceof TileEntityCanvasSign)) {
                return;
            }

            openEditor(world, pos, event.getEntityPlayer());
            event.setUseBlock(Event.Result.DENY);
            event.setCanceled(true);
            event.setCancellationResult(EnumActionResult.SUCCESS);
        }
    }
}
