package com.wdcftgg.farmersdelightlegacy.api.food;

import com.wdcftgg.farmersdelightlegacy.common.item.ItemDrinkableTooltip;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Public base item for addon drinks with configurable container return.
 */
public class AddonDrinkItem extends ItemDrinkableTooltip {

    public AddonDrinkItem(int amount, float saturation, boolean alwaysEdible, @Nullable ResourceLocation effectId,
                          int effectDuration, int effectAmplifier, float effectChance, DrinkEffect drinkEffect,
                          String... extraTooltipKeys) {
        super(amount, saturation, alwaysEdible, effectId, effectDuration, effectAmplifier, effectChance, drinkEffect, extraTooltipKeys);
    }

    public AddonDrinkItem(int amount, float saturation, boolean alwaysEdible, @Nullable ResourceLocation effectId,
                          int effectDuration, int effectAmplifier, float effectChance, DrinkEffect drinkEffect,
                          ItemStack containerItem, String... extraTooltipKeys) {
        super(amount, saturation, alwaysEdible, effectId, effectDuration, effectAmplifier, effectChance, drinkEffect, containerItem,
                extraTooltipKeys);
    }

    public AddonDrinkItem(int amount, float saturation, boolean alwaysEdible, List<FoodEffectEntry> foodEffects,
                          DrinkEffect drinkEffect, ItemStack containerItem, String... extraTooltipKeys) {
        super(amount, saturation, alwaysEdible, foodEffects, drinkEffect, containerItem, extraTooltipKeys);
    }

    public AddonDrinkItem(FoodItemApi.DrinkItemSettings settings) {
        super(normalize(settings).getNutrition(), normalize(settings).getSaturation(), normalize(settings).isAlwaysEdible(),
                normalize(settings).getFoodEffects(), normalize(settings).getDrinkEffect(), normalize(settings).getContainerItem(),
                normalize(settings).getExtraTooltipKeys());
        FoodItemApi.DrinkItemSettings value = normalize(settings);
        if (value.getMaxStackSize() > 0) {
            this.setMaxStackSize(value.getMaxStackSize());
        }
    }

    private static FoodItemApi.DrinkItemSettings normalize(FoodItemApi.DrinkItemSettings settings) {
        return settings == null ? FoodItemApi.DrinkItemSettings.builder().build() : settings;
    }
}