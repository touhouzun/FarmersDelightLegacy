package com.wdcftgg.farmersdelightlegacy.api.food;

import com.wdcftgg.farmersdelightlegacy.common.item.ItemDrinkableTooltip;
import com.wdcftgg.farmersdelightlegacy.common.item.ItemFoodTooltip;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Factory methods for addon food items.
 * <p>
 * Returned items still need registry names and registration in the addon's {@code RegistryEvent.Register<Item>}.
 */
public final class FoodItemApi {

    private FoodItemApi() {
    }

    /**
     * Creates a food item from the supplied settings.
     *
     * @param settings The settings object used to configure the created item or block; {@code null} uses defaults when supported.
     * @return The result produced by this API method.
     */
    public static ItemFoodTooltip createFood(FoodItemSettings settings) {
        FoodItemSettings value = settings == null ? FoodItemSettings.builder().build() : settings;
        return value.hasBowlContainer() ? new AddonBowlFoodItem(value) : new AddonFoodItem(value);
    }

    /**
     * Creates a drink item from the supplied settings.
     *
     * @param settings The settings object used to configure the created item or block; {@code null} uses defaults when supported.
     * @return The result produced by this API method.
     */
    public static ItemDrinkableTooltip createDrink(DrinkItemSettings settings) {
        return new AddonDrinkItem(settings == null ? DrinkItemSettings.builder().build() : settings);
    }
    /**
     * Creates a placeable multi-serving food block from the supplied settings.
     *
     * @param settings The settings object used to configure the created item or block; {@code null} uses defaults when supported.
     * @return The result produced by this API method.
     */
    public static PlaceableFoodBlock createPlaceableFoodBlock(PlaceableFoodSettings settings) {
        return new PlaceableFoodBlock(settings == null ? PlaceableFoodSettings.builder().build() : settings);
    }

    /**
     * Creates the item form for a placeable food block.
     *
     * @param block The block to wrap in an {@link net.minecraft.item.ItemBlock}.
     * @return The result produced by this API method.
     */
    public static ItemBlock createPlaceableFoodItemBlock(Block block) {
        ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setMaxStackSize(1);
        return itemBlock;
    }

    public static final class FoodItemSettings {
        private final int nutrition;
        private final float saturation;
        private final boolean alwaysEdible;
        private final boolean bowlContainer;
        private final List<ItemFoodTooltip.FoodEffectEntry> foodEffects;
        private final int maxStackSize;
        private final String[] extraTooltipKeys;

        private FoodItemSettings(Builder builder) {
            this.nutrition = Math.max(0, builder.nutrition);
            this.saturation = Math.max(0.0F, builder.saturation);
            this.alwaysEdible = builder.alwaysEdible;
            this.bowlContainer = builder.bowlContainer;
            this.foodEffects = Collections.unmodifiableList(new ArrayList<>(builder.foodEffects));
            this.maxStackSize = builder.maxStackSize;
            this.extraTooltipKeys = builder.extraTooltipKeys == null ? new String[0] : builder.extraTooltipKeys.clone();
        }

        /**
         * Creates a new settings builder.
         *
         * @return The result produced by this API method.
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Returns the configured hunger restoration value.
         *
         * @return The result produced by this API method.
         */
        public int getNutrition() {
            return nutrition;
        }

        /**
         * Returns the configured saturation modifier.
         *
         * @return The result produced by this API method.
         */
        public float getSaturation() {
            return saturation;
        }

        /**
         * Returns whether the item can be consumed while the player is full.
         *
         * @return The result produced by this API method.
         */
        public boolean isAlwaysEdible() {
            return alwaysEdible;
        }

        /**
         * Returns whether the food returns a bowl after eating.
         *
         * @return The result produced by this API method.
         */
        public boolean hasBowlContainer() {
            return bowlContainer;
        }

        /**
         * Returns the configured food effects.
         *
         * @return The result produced by this API method.
         */
        public List<ItemFoodTooltip.FoodEffectEntry> getFoodEffects() {
            return this.foodEffects;
        }

        /**
         * Returns the configured maximum stack size.
         *
         * @return The result produced by this API method.
         */
        public int getMaxStackSize() {
            return maxStackSize;
        }

        /**
         * Returns the configured extra tooltip translation keys.
         *
         * @return The result produced by this API method.
         */
        public String[] getExtraTooltipKeys() {
            return extraTooltipKeys.clone();
        }

        public static final class Builder {
            private int nutrition;
            private float saturation;
            private boolean alwaysEdible;
            private boolean bowlContainer;
            private final List<ItemFoodTooltip.FoodEffectEntry> foodEffects = new ArrayList<>();
            private int maxStackSize;
            private String[] extraTooltipKeys = new String[0];

            private Builder() {
            }

            /**
             * Sets the hunger restoration value.
             *
             * @param nutrition The hunger points restored by the configured food or drink.
             * @return The result produced by this API method.
             */
            public Builder nutrition(int nutrition) {
                this.nutrition = nutrition;
                return this;
            }

            /**
             * Sets the saturation modifier.
             *
             * @param saturation The saturation modifier used by the vanilla food system.
             * @return The result produced by this API method.
             */
            public Builder saturation(float saturation) {
                this.saturation = saturation;
                return this;
            }

            /**
             * Allows the item to be consumed while the player is full.
             *
             * @return The result produced by this API method.
             */
            public Builder alwaysEdible() {
                this.alwaysEdible = true;
                return this;
            }

            /**
             * Makes the food return a bowl after eating.
             *
             * @return The result produced by this API method.
             */
            public Builder bowlContainer() {
                this.bowlContainer = true;
                return this;
            }

            /**
             * Adds one food effect entry.
             *
             * @param effectId The potion effect registry id; {@code null} means no potion effect is added.
             * @param duration The effect duration in ticks.
             * @param amplifier The effect amplifier; 0 means level I.
             * @param chance The base chance for this output or effect, using the 0.0 to 1.0 range.
             * @return The result produced by this API method.
             */
            public Builder effect(@Nullable ResourceLocation effectId, int duration, int amplifier, float chance) {
                if (effectId != null) {
                    this.foodEffects.add(new ItemFoodTooltip.FoodEffectEntry(effectId, duration, amplifier, chance));
                }
                return this;
            }

            /**
             * Adds one food effect entry.
             *
             * @param effectEntry The complete food effect entry to add; {@code null} is ignored.
             * @return The result produced by this API method.
             */
            public Builder effect(ItemFoodTooltip.FoodEffectEntry effectEntry) {
                if (effectEntry != null) {
                    this.foodEffects.add(effectEntry);
                }
                return this;
            }

            /**
             * Replaces all food effect entries.
             *
             * @param effectEntries The replacement food effect list; {@code null} clears the list.
             * @return The result produced by this API method.
             */
            public Builder effects(List<ItemFoodTooltip.FoodEffectEntry> effectEntries) {
                this.foodEffects.clear();
                if (effectEntries != null) {
                    this.foodEffects.addAll(effectEntries);
                }
                return this;
            }

            /**
             * Sets the maximum stack size.
             *
             * @param maxStackSize The maximum stack size; values less than or equal to 0 keep the item default.
             * @return The result produced by this API method.
             */
            public Builder maxStackSize(int maxStackSize) {
                this.maxStackSize = maxStackSize;
                return this;
            }

            /**
             * Sets extra tooltip translation keys.
             *
             * @param extraTooltipKeys Translation keys appended to the item tooltip.
             * @return The result produced by this API method.
             */
            public Builder extraTooltipKeys(String... extraTooltipKeys) {
                this.extraTooltipKeys = extraTooltipKeys == null ? new String[0] : extraTooltipKeys.clone();
                return this;
            }

            /**
             * Builds the immutable settings object.
             *
             * @return The result produced by this API method.
             */
            public FoodItemSettings build() {
                return new FoodItemSettings(this);
            }
        }
    }

    public static final class DrinkItemSettings {
        private final int nutrition;
        private final float saturation;
        private final boolean alwaysEdible;
        private final List<ItemFoodTooltip.FoodEffectEntry> foodEffects;
        private final ItemDrinkableTooltip.DrinkEffect drinkEffect;
        private final ItemStack containerItem;
        private final int maxStackSize;
        private final String[] extraTooltipKeys;

        private DrinkItemSettings(Builder builder) {
            this.nutrition = Math.max(0, builder.nutrition);
            this.saturation = Math.max(0.0F, builder.saturation);
            this.alwaysEdible = builder.alwaysEdible;
            this.foodEffects = Collections.unmodifiableList(new ArrayList<>(builder.foodEffects));
            this.drinkEffect = builder.drinkEffect == null ? ItemDrinkableTooltip.DrinkEffect.NONE : builder.drinkEffect;
            this.containerItem = builder.containerItem == null || builder.containerItem.isEmpty()
                    ? new ItemStack(net.minecraft.init.Items.GLASS_BOTTLE) : builder.containerItem.copy();
            this.maxStackSize = builder.maxStackSize;
            this.extraTooltipKeys = builder.extraTooltipKeys == null ? new String[0] : builder.extraTooltipKeys.clone();
        }

        /**
         * Creates a new settings builder.
         *
         * @return The result produced by this API method.
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Returns the configured hunger restoration value.
         *
         * @return The result produced by this API method.
         */
        public int getNutrition() {
            return nutrition;
        }

        /**
         * Returns the configured saturation modifier.
         *
         * @return The result produced by this API method.
         */
        public float getSaturation() {
            return saturation;
        }

        /**
         * Returns whether the item can be consumed while the player is full.
         *
         * @return The result produced by this API method.
         */
        public boolean isAlwaysEdible() {
            return alwaysEdible;
        }

        /**
         * Returns the configured food effects.
         *
         * @return The result produced by this API method.
         */
        public List<ItemFoodTooltip.FoodEffectEntry> getFoodEffects() {
            return this.foodEffects;
        }

        /**
         * Returns the configured drink behavior.
         *
         * @return The result produced by this API method.
         */
        public ItemDrinkableTooltip.DrinkEffect getDrinkEffect() {
            return drinkEffect;
        }

        /**
         * Returns the configured returned container stack.
         *
         * @return The result produced by this API method.
         */
        public ItemStack getContainerItem() {
            return this.containerItem.copy();
        }

        /**
         * Returns the configured maximum stack size.
         *
         * @return The result produced by this API method.
         */
        public int getMaxStackSize() {
            return maxStackSize;
        }

        /**
         * Returns the configured extra tooltip translation keys.
         *
         * @return The result produced by this API method.
         */
        public String[] getExtraTooltipKeys() {
            return extraTooltipKeys.clone();
        }

        public static final class Builder {
            private int nutrition;
            private float saturation;
            private boolean alwaysEdible;
            private final List<ItemFoodTooltip.FoodEffectEntry> foodEffects = new ArrayList<>();
            private ItemDrinkableTooltip.DrinkEffect drinkEffect = ItemDrinkableTooltip.DrinkEffect.NONE;
            private ItemStack containerItem = new ItemStack(net.minecraft.init.Items.GLASS_BOTTLE);
            private int maxStackSize;
            private String[] extraTooltipKeys = new String[0];

            private Builder() {
            }

            /**
             * Sets the hunger restoration value.
             *
             * @param nutrition The hunger points restored by the configured food or drink.
             * @return The result produced by this API method.
             */
            public Builder nutrition(int nutrition) {
                this.nutrition = nutrition;
                return this;
            }

            /**
             * Sets the saturation modifier.
             *
             * @param saturation The saturation modifier used by the vanilla food system.
             * @return The result produced by this API method.
             */
            public Builder saturation(float saturation) {
                this.saturation = saturation;
                return this;
            }

            /**
             * Allows the item to be consumed while the player is full.
             *
             * @return The result produced by this API method.
             */
            public Builder alwaysEdible() {
                this.alwaysEdible = true;
                return this;
            }

            /**
             * Adds one food effect entry.
             *
             * @param effectId The potion effect registry id; {@code null} means no potion effect is added.
             * @param duration The effect duration in ticks.
             * @param amplifier The effect amplifier; 0 means level I.
             * @param chance The base chance for this output or effect, using the 0.0 to 1.0 range.
             * @return The result produced by this API method.
             */
            public Builder effect(@Nullable ResourceLocation effectId, int duration, int amplifier, float chance) {
                if (effectId != null) {
                    this.foodEffects.add(new ItemFoodTooltip.FoodEffectEntry(effectId, duration, amplifier, chance));
                }
                return this;
            }

            /**
             * Adds one food effect entry.
             *
             * @param effectEntry The complete food effect entry to add; {@code null} is ignored.
             * @return The result produced by this API method.
             */
            public Builder effect(ItemFoodTooltip.FoodEffectEntry effectEntry) {
                if (effectEntry != null) {
                    this.foodEffects.add(effectEntry);
                }
                return this;
            }

            /**
             * Replaces all food effect entries.
             *
             * @param effectEntries The replacement food effect list; {@code null} clears the list.
             * @return The result produced by this API method.
             */
            public Builder effects(List<ItemFoodTooltip.FoodEffectEntry> effectEntries) {
                this.foodEffects.clear();
                if (effectEntries != null) {
                    this.foodEffects.addAll(effectEntries);
                }
                return this;
            }

            /**
             * Sets the drink behavior.
             *
             * @param drinkEffect Extra behavior executed after drinking, such as clearing effects.
             * @return The result produced by this API method.
             */
            public Builder drinkEffect(ItemDrinkableTooltip.DrinkEffect drinkEffect) {
                this.drinkEffect = drinkEffect;
                return this;
            }

            /**
             * Sets the returned container stack.
             *
             * @param containerItem The item stack returned after drinking; an empty stack returns no container.
             * @return The result produced by this API method.
             */
            public Builder containerItem(ItemStack containerItem) {
                this.containerItem = containerItem == null ? ItemStack.EMPTY : containerItem.copy();
                return this;
            }

            /**
             * Sets the maximum stack size.
             *
             * @param maxStackSize The maximum stack size; values less than or equal to 0 keep the item default.
             * @return The result produced by this API method.
             */
            public Builder maxStackSize(int maxStackSize) {
                this.maxStackSize = maxStackSize;
                return this;
            }

            /**
             * Sets extra tooltip translation keys.
             *
             * @param extraTooltipKeys Translation keys appended to the item tooltip.
             * @return The result produced by this API method.
             */
            public Builder extraTooltipKeys(String... extraTooltipKeys) {
                this.extraTooltipKeys = extraTooltipKeys == null ? new String[0] : extraTooltipKeys.clone();
                return this;
            }

            /**
             * Builds the immutable settings object.
             *
             * @return The result produced by this API method.
             */
            public DrinkItemSettings build() {
                return new DrinkItemSettings(this);
            }
        }
    }

    public static final class PlaceableFoodSettings {
        private final int maxServings;
        private final String servingItemPath;
        @Nullable
        private final String requiredContainerPath;
        private final boolean hasLeftovers;
        private final float hardness;
        private final float resistance;

        private PlaceableFoodSettings(Builder builder) {
            this.maxServings = Math.max(1, builder.maxServings);
            this.servingItemPath = builder.servingItemPath == null ? "" : builder.servingItemPath;
            this.requiredContainerPath = builder.requiredContainerPath;
            this.hasLeftovers = builder.hasLeftovers;
            this.hardness = builder.hardness;
            this.resistance = builder.resistance;
        }

        /**
         * Creates a new settings builder.
         *
         * @return The result produced by this API method.
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Returns the configured maximum serving count.
         *
         * @return The result produced by this API method.
         */
        public int getMaxServings() {
            return maxServings;
        }

        /**
         * Returns the configured serving item registry name.
         *
         * @return The result produced by this API method.
         */
        public String getServingItemPath() {
            return servingItemPath;
        }

        @Nullable
        /**
         * Returns the configured required container registry name.
         *
         * @return The result produced by this API method.
         */
        public String getRequiredContainerPath() {
            return requiredContainerPath;
        }

        /**
         * Returns whether the block keeps leftovers after the final serving.
         *
         * @return The result produced by this API method.
         */
        public boolean hasLeftovers() {
            return hasLeftovers;
        }

        /**
         * Returns the configured block hardness.
         *
         * @return The result produced by this API method.
         */
        public float getHardness() {
            return hardness;
        }

        /**
         * Returns the configured block blast resistance.
         *
         * @return The result produced by this API method.
         */
        public float getResistance() {
            return resistance;
        }

        public static final class Builder {
            private int maxServings = 4;
            private String servingItemPath = "";
            @Nullable
            private String requiredContainerPath = "minecraft:bowl";
            private boolean hasLeftovers = true;
            private float hardness = 0.8F;
            private float resistance = 1.0F;

            private Builder() {
            }

            /**
             * Sets the maximum serving count.
             *
             * @param maxServings The maximum number of servings the block can provide.
             * @return The result produced by this API method.
             */
            public Builder maxServings(int maxServings) {
                this.maxServings = maxServings;
                return this;
            }

            /**
             * Sets the serving item registry name.
             *
             * @param servingItemPath The registry name string of the item given for each serving.
             * @return The result produced by this API method.
             */
            public Builder servingItem(String servingItemPath) {
                this.servingItemPath = servingItemPath;
                return this;
            }

            /**
             * Sets the required serving container registry name.
             *
             * @param requiredContainerPath The registry name string of the required serving container; {@code null} means no container is required.
             * @return The result produced by this API method.
             */
            public Builder requiredContainer(@Nullable String requiredContainerPath) {
                this.requiredContainerPath = requiredContainerPath;
                return this;
            }

            /**
             * Removes the required serving container.
             *
             * @return The result produced by this API method.
             */
            public Builder noRequiredContainer() {
                this.requiredContainerPath = null;
                return this;
            }

            /**
             * Returns whether the block keeps leftovers after the final serving.
             *
             * @param hasLeftovers Whether the block keeps a leftover stage after the final serving is taken.
             * @return The result produced by this API method.
             */
            public Builder hasLeftovers(boolean hasLeftovers) {
                this.hasLeftovers = hasLeftovers;
                return this;
            }

            /**
             * Sets the block hardness and blast resistance.
             *
             * @param hardness The block hardness value.
             * @param resistance The block blast resistance value.
             * @return The result produced by this API method.
             */
            public Builder strength(float hardness, float resistance) {
                this.hardness = hardness;
                this.resistance = resistance;
                return this;
            }

            /**
             * Builds the immutable settings object.
             *
             * @return The result produced by this API method.
             */
            public PlaceableFoodSettings build() {
                return new PlaceableFoodSettings(this);
            }
        }
    }

    public static class PlaceableFoodBlock extends AddonPlaceableFoodBlock {

        /**
         * Creates a placeable multi-serving food block from settings.
         *
         * @param settings The settings object used to configure the created item or block; {@code null} uses defaults when supported.
         */
        public PlaceableFoodBlock(FoodItemApi.PlaceableFoodSettings settings) {
            super(settings);
        }
    }
}
