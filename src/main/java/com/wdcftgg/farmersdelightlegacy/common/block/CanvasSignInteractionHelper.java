package com.wdcftgg.farmersdelightlegacy.common.block;

import com.wdcftgg.farmersdelightlegacy.common.network.ModNetworkHandler;
import com.wdcftgg.farmersdelightlegacy.common.network.PacketOpenCanvasSignEditor;
import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityCanvasSign;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
}
