package com.wdcftgg.farmersdelightlegacy.common.compat.futuremc;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class FutureMcComposterCompat {

    private static final String FUTURE_MC_MOD_ID = "futuremc";
    private static final String COMPOSTER_ITEMS_CLASS = "thedarkcolour.futuremc.block.villagepillage.ComposterBlock$ItemsForComposter";

    private static final CompostableEntry[] COMPOSTABLES = {
            new CompostableEntry("tree_bark", 30),
            new CompostableEntry("straw", 30),
            new CompostableEntry("cabbage_seeds", 30),
            new CompostableEntry("tomato_seeds", 30),
            new CompostableEntry("rice", 30),
            new CompostableEntry("rice_panicle", 30),
            new CompostableEntry("sandy_shrub", 30),

            new CompostableEntry("pumpkin_slice", 50),
            new CompostableEntry("cabbage_leaf", 50),
            new CompostableEntry("kelp_roll_slice", 50),

            new CompostableEntry("cabbage", 65),
            new CompostableEntry("onion", 65),
            new CompostableEntry("tomato", 65),
            new CompostableEntry("wild_cabbages", 65),
            new CompostableEntry("wild_onions", 65),
            new CompostableEntry("wild_tomatoes", 65),
            new CompostableEntry("wild_carrots", 65),
            new CompostableEntry("wild_potatoes", 65),
            new CompostableEntry("wild_beetroots", 65),
            new CompostableEntry("wild_rice", 65),
            new CompostableEntry("pie_crust", 65),

            new CompostableEntry("rice_bale", 85),
            new CompostableEntry("sweet_berry_cookie", 85),
            new CompostableEntry("honey_cookie", 85),
            new CompostableEntry("cake_slice", 85),
            new CompostableEntry("apple_pie_slice", 85),
            new CompostableEntry("sweet_berry_cheesecake_slice", 85),
            new CompostableEntry("chocolate_pie_slice", 85),
            new CompostableEntry("raw_pasta", 85),
            new CompostableEntry("rotten_tomato", 85),
            new CompostableEntry("kelp_roll", 85),

            new CompostableEntry("apple_pie", 100),
            new CompostableEntry("sweet_berry_cheesecake", 100),
            new CompostableEntry("chocolate_pie", 100),
            new CompostableEntry("dumplings", 100),
            new CompostableEntry("stuffed_pumpkin_block", 100),
            new CompostableEntry("brown_mushroom_colony", 100),
            new CompostableEntry("red_mushroom_colony", 100)
    };

    private FutureMcComposterCompat() {
    }

    public static void registerAll() {
        if (!Loader.isModLoaded(FUTURE_MC_MOD_ID)) {
            return;
        }

        try {
            Class<?> composterItemsClass = Class.forName(COMPOSTER_ITEMS_CLASS);
            Object composterItems = getCompanionObject(composterItemsClass);
            Method addMethod = composterItemsClass.getMethod("add", ItemStack.class, int.class);
            int registeredCount = 0;

            for (CompostableEntry entry : COMPOSTABLES) {
                Item item = Item.getByNameOrId(FarmersDelightLegacy.MOD_ID + ":" + entry.itemPath);
                if (item == null) {
                    FarmersDelightLegacy.LOGGER.warn("Future MC composter skipped missing Farmers Delight item: {}", entry.itemPath);
                    continue;
                }
                addMethod.invoke(composterItems, new ItemStack(item), entry.chancePercent);
                registeredCount++;
            }

            FarmersDelightLegacy.LOGGER.info("Registered {} Farmers Delight compostables for Future MC composter.", registeredCount);
        } catch (ReflectiveOperationException exception) {
            FarmersDelightLegacy.LOGGER.error("Failed to register Future MC composter entries.", exception);
        }
    }

    private static Object getCompanionObject(Class<?> composterItemsClass) throws ReflectiveOperationException {
        Field instanceField = composterItemsClass.getField("INSTANCE");
        return instanceField.get(null);
    }

    private static final class CompostableEntry {
        private final String itemPath;
        private final int chancePercent;

        private CompostableEntry(String itemPath, int chancePercent) {
            this.itemPath = itemPath;
            this.chancePercent = chancePercent;
        }
    }
}