package com.wdcftgg.farmersdelightlegacy.api.food;

import com.wdcftgg.farmersdelightlegacy.common.item.ItemFoodTooltip;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Public base item for addon food with Farmer's Delight tooltip and effect behavior.
 */
public class AddonFoodItem extends ItemFoodTooltip {

    public AddonFoodItem(int amount, float saturation, boolean isWolfFood) {
        super(amount, saturation, isWolfFood);
    }

    public AddonFoodItem(int amount, float saturation, boolean isWolfFood, @Nullable ResourceLocation effectId,
                         int effectDuration, int effectAmplifier, float effectChance, String... extraTooltipKeys) {
        super(amount, saturation, isWolfFood, effectId, effectDuration, effectAmplifier, effectChance, extraTooltipKeys);
    }

    public AddonFoodItem(int amount, float saturation, boolean isWolfFood, List<FoodEffectEntry> foodEffects,
                         String... extraTooltipKeys) {
        super(amount, saturation, isWolfFood, foodEffects, extraTooltipKeys);
    }

    public AddonFoodItem(FoodItemApi.FoodItemSettings settings) {
        this(settings == null ? FoodItemApi.FoodItemSettings.builder().build() : settings, false);
    }

    protected AddonFoodItem(FoodItemApi.FoodItemSettings settings, boolean forceBowlContainer) {
        super(settings.getNutrition(), settings.getSaturation(), false, settings.getFoodEffects(), settings.getExtraTooltipKeys());
        applySettings(settings);
    }

    protected final void applySettings(FoodItemApi.FoodItemSettings settings) {
        if (settings.isAlwaysEdible()) {
            this.setAlwaysEdible();
        }
        if (settings.getMaxStackSize() > 0) {
            this.setMaxStackSize(settings.getMaxStackSize());
        }
    }
}