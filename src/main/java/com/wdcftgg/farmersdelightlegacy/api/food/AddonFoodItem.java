package com.wdcftgg.farmersdelightlegacy.api.food;

import com.wdcftgg.farmersdelightlegacy.common.item.ItemFoodTooltip;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Public base item for addon food with Farmer's Delight tooltip and effect behavior.
 */
public class AddonFoodItem extends ItemFoodTooltip {

    /**
     * Creates a food item.
     *
     * @param amount The hunger points restored by this item.
     * @param saturation The saturation modifier used by the vanilla food system.
     * @param isWolfFood Whether wolves can eat this item.
     */
    public AddonFoodItem(int amount, float saturation, boolean isWolfFood) {
        super(amount, saturation, isWolfFood);
    }

    /**
     * Creates a food item.
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
    public AddonFoodItem(int amount, float saturation, boolean isWolfFood, @Nullable ResourceLocation effectId,
                         int effectDuration, int effectAmplifier, float effectChance, String... extraTooltipKeys) {
        super(amount, saturation, isWolfFood, effectId, effectDuration, effectAmplifier, effectChance, extraTooltipKeys);
    }

    /**
     * Creates a food item.
     *
     * @param amount The hunger points restored by this item.
     * @param saturation The saturation modifier used by the vanilla food system.
     * @param isWolfFood Whether wolves can eat this item.
     * @param foodEffects Food effect entries attempted when the item is consumed.
     * @param extraTooltipKeys Translation keys appended to the item tooltip.
     */
    public AddonFoodItem(int amount, float saturation, boolean isWolfFood, List<FoodEffectEntry> foodEffects,
                         String... extraTooltipKeys) {
        super(amount, saturation, isWolfFood, foodEffects, extraTooltipKeys);
    }

    /**
     * Creates a food item.
     *
     * @param settings The settings object used to configure the created item or block; {@code null} uses defaults when supported.
     */
    public AddonFoodItem(FoodItemApi.FoodItemSettings settings) {
        this(settings == null ? FoodItemApi.FoodItemSettings.builder().build() : settings, false);
    }

    /**
     * Creates a food item from settings for subclasses that need to control container behavior.
     *
     * @param settings           The food settings used to configure the item.
     * @param forceBowlContainer Internal flag reserved for forcing bowl-container behavior in subclasses.
     */
    protected AddonFoodItem(FoodItemApi.FoodItemSettings settings, boolean forceBowlContainer) {
        super(settings.getNutrition(), settings.getSaturation(), false, settings.getFoodEffects(), settings.getExtraTooltipKeys());
        applySettings(settings);
    }

    /**
     * Applies shared item settings after the base food constructor has run.
     *
     * @param settings The food settings to apply to this item.
     */
    protected final void applySettings(FoodItemApi.FoodItemSettings settings) {
        if (settings.isAlwaysEdible()) {
            this.setAlwaysEdible();
        }
        if (settings.getMaxStackSize() > 0) {
            this.setMaxStackSize(settings.getMaxStackSize());
        }
    }
}
