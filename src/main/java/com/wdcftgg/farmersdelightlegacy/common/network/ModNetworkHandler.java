package com.wdcftgg.farmersdelightlegacy.common.network;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class ModNetworkHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(FarmersDelightLegacy.MOD_ID);

    private ModNetworkHandler() {
    }

    public static void registerAll() {
        INSTANCE.registerMessage(PacketOpenCanvasSignEditor.Handler.class, PacketOpenCanvasSignEditor.class, 0, Side.CLIENT);
    }
}
