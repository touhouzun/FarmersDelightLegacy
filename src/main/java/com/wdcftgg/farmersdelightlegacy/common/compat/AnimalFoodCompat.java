package com.wdcftgg.farmersdelightlegacy.common.compat;

import com.wdcftgg.farmersdelightlegacy.common.registry.ModItems;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.Set;

public final class AnimalFoodCompat {

    private static final String CHICKEN_TEMPTATION_ITEMS_FIELD = "field_184761_bD";
    private static final String PIG_TEMPTATION_ITEMS_FIELD = "field_184764_bw";

    private AnimalFoodCompat() {
    }

    public static void registerAll() {
        addChickenFood("cabbage_seeds");
        addChickenFood("tomato_seeds");
        addChickenFood("rice");

        addPigFood("cabbage");
        addPigFood("tomato");
    }

    private static void addChickenFood(String itemPath) {
        Item item = ModItems.ITEMS.get(itemPath);
        if (item != null) {
            getChickenTemptationItems().add(item);
        }
    }

    private static void addPigFood(String itemPath) {
        Item item = ModItems.ITEMS.get(itemPath);
        if (item != null) {
            getPigTemptationItems().add(item);
        }
    }

    @SuppressWarnings("unchecked")
    private static Set<Item> getChickenTemptationItems() {
        return ObfuscationReflectionHelper.getPrivateValue(EntityChicken.class, null, CHICKEN_TEMPTATION_ITEMS_FIELD);
    }

    @SuppressWarnings("unchecked")
    private static Set<Item> getPigTemptationItems() {
        return ObfuscationReflectionHelper.getPrivateValue(EntityPig.class, null, PIG_TEMPTATION_ITEMS_FIELD);
    }
}
