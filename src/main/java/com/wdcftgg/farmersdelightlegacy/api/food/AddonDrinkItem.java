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

    /**
     * Creates a drink item.
     *
     * @param amount The hunger points restored by this item.
     * @param saturation The saturation modifier used by the vanilla food system.
     * @param alwaysEdible Whether the item can be consumed when the player is already full.
     * @param effectId The potion effect registry id; {@code null} means no potion effect is added.
     * @param effectDuration The potion effect duration in ticks.
     * @param effectAmplifier The potion effect amplifier; 0 means level I.
     * @param effectChance The chance for the potion effect to apply, using the 0.0 to 1.0 range.
     * @param drinkEffect Extra behavior executed after drinking, such as clearing effects.
     * @param extraTooltipKeys Translation keys appended to the item tooltip.
     */
    public AddonDrinkItem(int amount, float saturation, boolean alwaysEdible, @Nullable ResourceLocation effectId,
                          int effectDuration, int effectAmplifier, float effectChance, DrinkEffect drinkEffect,
                          String... extraTooltipKeys) {
        super(amount, saturation, alwaysEdible, effectId, effectDuration, effectAmplifier, effectChance, drinkEffect, extraTooltipKeys);
    }

    /**
     * Creates a drink item.
     *
     * @param amount The hunger points restored by this item.
     * @param saturation The saturation modifier used by the vanilla food system.
     * @param alwaysEdible Whether the item can be consumed when the player is already full.
     * @param effectId The potion effect registry id; {@code null} means no potion effect is added.
     * @param effectDuration The potion effect duration in ticks.
     * @param effectAmplifier The potion effect amplifier; 0 means level I.
     * @param effectChance The chance for the potion effect to apply, using the 0.0 to 1.0 range.
     * @param drinkEffect Extra behavior executed after drinking, such as clearing effects.
     * @param containerItem The item stack returned after drinking; an empty stack returns no container.
     * @param extraTooltipKeys Translation keys appended to the item tooltip.
     */
    public AddonDrinkItem(int amount, float saturation, boolean alwaysEdible, @Nullable ResourceLocation effectId,
                          int effectDuration, int effectAmplifier, float effectChance, DrinkEffect drinkEffect,
                          ItemStack containerItem, String... extraTooltipKeys) {
        super(amount, saturation, alwaysEdible, effectId, effectDuration, effectAmplifier, effectChance, drinkEffect, containerItem,
                extraTooltipKeys);
    }

    /**
     * Creates a drink item.
     *
     * @param amount The hunger points restored by this item.
     * @param saturation The saturation modifier used by the vanilla food system.
     * @param alwaysEdible Whether the item can be consumed when the player is already full.
     * @param foodEffects Food effect entries attempted when the item is consumed.
     * @param drinkEffect Extra behavior executed after drinking, such as clearing effects.
     * @param containerItem The item stack returned after drinking; an empty stack returns no container.
     * @param extraTooltipKeys Translation keys appended to the item tooltip.
     */
    public AddonDrinkItem(int amount, float saturation, boolean alwaysEdible, List<FoodEffectEntry> foodEffects,
                          DrinkEffect drinkEffect, ItemStack containerItem, String... extraTooltipKeys) {
        super(amount, saturation, alwaysEdible, foodEffects, drinkEffect, containerItem, extraTooltipKeys);
    }

    /**
     * Creates a drink item.
     *
     * @param settings The settings object used to configure the created item or block; {@code null} uses defaults when supported.
     */
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
