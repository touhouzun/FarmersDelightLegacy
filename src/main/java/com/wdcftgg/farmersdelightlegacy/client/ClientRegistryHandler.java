package com.wdcftgg.farmersdelightlegacy.client;

import com.wdcftgg.farmersdelight.Tags;
import com.wdcftgg.farmersdelightlegacy.client.render.TileEntityCuttingBoardRenderer;
import com.wdcftgg.farmersdelightlegacy.client.render.TileEntityCanvasSignRenderer;
import com.wdcftgg.farmersdelightlegacy.client.render.TileEntitySkilletRenderer;
import com.wdcftgg.farmersdelightlegacy.client.render.TileEntityStoveRenderer;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModItems;
import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityCanvasSign;
import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityCuttingBoard;
import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntitySkillet;
import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityStove;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID, value = Side.CLIENT)
public final class ClientRegistryHandler {

    private static boolean tileRenderersBound;

    private ClientRegistryHandler() {
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        if (!tileRenderersBound) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCuttingBoard.class, new TileEntityCuttingBoardRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySkillet.class, new TileEntitySkilletRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStove.class, new TileEntityStoveRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCanvasSign.class, new TileEntityCanvasSignRenderer());
            tileRenderersBound = true;
        }

        for (Block block : ModBlocks.BLOCKS.values()) {
            if (block instanceof BlockStandingSign) {
                ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockStandingSign.ROTATION).build());
            } else if (block instanceof BlockWallSign) {
                ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockWallSign.FACING).build());
            }
        }

        for (Item item : ModItems.ITEMS.values()) {
            if (item.getRegistryName() == null) {
                continue;
            }
            ResourceLocation location = item.getRegistryName();
            ModelLoader.setCustomModelResourceLocation(item, 0, new net.minecraft.client.renderer.block.model.ModelResourceLocation(location, "inventory"));
        }
    }
}

