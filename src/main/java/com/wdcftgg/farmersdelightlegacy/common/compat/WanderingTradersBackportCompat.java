package com.wdcftgg.farmersdelightlegacy.common.compat;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.common.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class WanderingTradersBackportCompat {
    private static final String TRADERS_MOD_ID = "traders";
    private static final String TRADE_FILE_NAME = "farmersdelightlegacy.json";

    private WanderingTradersBackportCompat() {
    }

    public static void syncTradeTable(FMLPreInitializationEvent event) {
        syncTradeTable(event.getModConfigurationDirectory());
    }

    public static void syncTradeTable(File configDirectory) {
        if (!Loader.isModLoaded(TRADERS_MOD_ID) || configDirectory == null) {
            return;
        }

        File tradeDirectory = new File(configDirectory, "traders/trades");
        File tradeFile = new File(tradeDirectory, TRADE_FILE_NAME);
        if (!Configuration.wanderingTraderSellsFDItems) {
            deleteTradeTable(tradeFile);
            return;
        }

        if (!tradeDirectory.exists() && !tradeDirectory.mkdirs()) {
            FarmersDelightLegacy.LOGGER.warn("Unable to create Wandering Traders Backport trade directory: {}", tradeDirectory);
            return;
        }

        try (FileWriter writer = new FileWriter(tradeFile)) {
            writer.write(createTradeTableJson());
        } catch (IOException exception) {
            FarmersDelightLegacy.LOGGER.warn("Unable to write Wandering Traders Backport Farmer's Delight trade table: {}", tradeFile, exception);
        }
    }

    private static void deleteTradeTable(File tradeFile) {
        if (tradeFile.exists() && !tradeFile.delete()) {
            FarmersDelightLegacy.LOGGER.warn("Unable to delete disabled Wandering Traders Backport trade table: {}", tradeFile);
        }
    }

    private static String createTradeTableJson() {
        return "{\n"
                + "  \"min\": 1,\n"
                + "  \"max\": 1,\n"
                + "  \"conditions\": [\n"
                + "    {\n"
                + "      \"condition\": \"mod_installed\",\n"
                + "      \"value\": \"" + FarmersDelightLegacy.MOD_ID + "\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"trades\": [\n"
                + createItemForEmeraldTrade("cabbage_seeds", false)
                + ",\n"
                + createItemForEmeraldTrade("tomato_seeds", false)
                + ",\n"
                + createItemForEmeraldTrade("rice", false)
                + ",\n"
                + createItemForEmeraldTrade("onion", true)
                + "\n"
                + "  ]\n"
                + "}\n";
    }

    private static String createItemForEmeraldTrade(String itemName, boolean canDuplicate) {
        return "    {\n"
                + "      \"item_1\": {\n"
                + "        \"item\": \"minecraft:emerald\"\n"
                + "      },\n"
                + "      \"output\": {\n"
                + "        \"item\": \"" + FarmersDelightLegacy.MOD_ID + ":" + itemName + "\"\n"
                + "      },\n"
                + "      \"max_uses\": 12,\n"
                + "      \"can_duplicate\": " + canDuplicate + "\n"
                + "    }";
    }
}
