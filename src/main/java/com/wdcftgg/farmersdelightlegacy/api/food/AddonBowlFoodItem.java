package com.wdcftgg.farmersdelightlegacy.api.food;

import com.wdcftgg.farmersdelightlegacy.common.item.ItemBowlFoodTooltip;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Public base item for addon bowl foods that return a bowl after eating.
 */
public class AddonBowlFoodItem extends ItemBowlFoodTooltip {

    /**
     * Creates a bowl food item.
     *
     * @param amount The hunger points restored by this item.
     * @param saturation The saturation modifier used by the vanilla food system.
     * @param isWolfFood Whether wolves can eat this item.
     */
    public AddonBowlFoodItem(int amount, float saturation, boolean isWolfFood) {
        super(amount, saturation, isWolfFood);
    }

    /**
     * Creates a bowl food item.
     *
     * @param amount The hunger points restored by this item.
     * @param saturation The saturation modifier used by the vanilla food system.
     * @param isWolfFood Whether wolves can eat this item.
     * @param effectId The potion effect registry id; {@code null} means no potion effect is added.
     * @param effectDuration The potion effect duration in ticks.
     * @param effectAmplifier The potion effect amplifier; 0 means level I.
     * @param effectChance The chance for the potion effect to apply, using the 0.0 to 1.0 range.
     * @param extraTooltipKeys Translation keys appended to the item tooltip.
     */
    public AddonBowlFoodItem(int amount, float saturation, boolean isWolfFood, @Nullable ResourceLocation effectId,
                             int effectDuration, int effectAmplifier, float effectChance, String... extraTooltipKeys) {
        super(amount, saturation, isWolfFood, effectId, effectDuration, effectAmplifier, effectChance, extraTooltipKeys);
    }

    /**
     * Creates a bowl food item.
     *
     * @param amount The hunger points restored by this item.
     * @param saturation The saturation modifier used by the vanilla food system.
     * @param isWolfFood Whether wolves can eat this item.
     * @param foodEffects Food effect entries attempted when the item is consumed.
     * @param extraTooltipKeys Translation keys appended to the item tooltip.
     */
    public AddonBowlFoodItem(int amount, float saturation, boolean isWolfFood, List<FoodEffectEntry> foodEffects,
                             String... extraTooltipKeys) {
        super(amount, saturation, isWolfFood, foodEffects, extraTooltipKeys);
    }

    /**
     * Creates a bowl food item.
     *
     * @param settings The settings object used to configure the created item or block; {@code null} uses defaults when supported.
     */
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
