package com.wdcftgg.farmersdelightlegacy.api.food;

import com.wdcftgg.farmersdelightlegacy.common.item.ItemBowlFoodTooltip;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Public base item for addon bowl foods that return a bowl after eating.
 */
public class AddonBowlFoodItem extends ItemBowlFoodTooltip {

    public AddonBowlFoodItem(int amount, float saturation, boolean isWolfFood) {
        super(amount, saturation, isWolfFood);
    }

    public AddonBowlFoodItem(int amount, float saturation, boolean isWolfFood, @Nullable ResourceLocation effectId,
                             int effectDuration, int effectAmplifier, float effectChance, String... extraTooltipKeys) {
        super(amount, saturation, isWolfFood, effectId, effectDuration, effectAmplifier, effectChance, extraTooltipKeys);
    }

    public AddonBowlFoodItem(int amount, float saturation, boolean isWolfFood, List<FoodEffectEntry> foodEffects,
                             String... extraTooltipKeys) {
        super(amount, saturation, isWolfFood, foodEffects, extraTooltipKeys);
    }

    public AddonBowlFoodItem(FoodItemApi.FoodItemSettings settings) {
        super(normalize(settings).getNutrition(), normalize(settings).getSaturation(), false,
                normalize(settings).getFoodEffects(), normalize(settings).getExtraTooltipKeys());
        FoodItemApi.FoodItemSettings value = normalize(settings);
        if (value.isAlwaysEdible()) {
            this.setAlwaysEdible();
        }
        if (value.getMaxStackSize() > 0) {
            this.setMaxStackSize(value.getMaxStackSize());
        }
    }

    private static FoodItemApi.FoodItemSettings normalize(FoodItemApi.FoodItemSettings settings) {
        return settings == null ? FoodItemApi.FoodItemSettings.builder().build() : settings;
    }
}