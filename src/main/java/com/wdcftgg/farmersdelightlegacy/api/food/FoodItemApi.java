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

    public static ItemFoodTooltip createFood(FoodItemSettings settings) {
        FoodItemSettings value = settings == null ? FoodItemSettings.builder().build() : settings;
        return value.hasBowlContainer() ? new AddonBowlFoodItem(value) : new AddonFoodItem(value);
    }

    public static ItemDrinkableTooltip createDrink(DrinkItemSettings settings) {
        return new AddonDrinkItem(settings == null ? DrinkItemSettings.builder().build() : settings);
    }
    public static PlaceableFoodBlock createPlaceableFoodBlock(PlaceableFoodSettings settings) {
        return new PlaceableFoodBlock(settings == null ? PlaceableFoodSettings.builder().build() : settings);
    }

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

        public static Builder builder() {
            return new Builder();
        }

        public int getNutrition() {
            return nutrition;
        }

        public float getSaturation() {
            return saturation;
        }

        public boolean isAlwaysEdible() {
            return alwaysEdible;
        }

        public boolean hasBowlContainer() {
            return bowlContainer;
        }

        public List<ItemFoodTooltip.FoodEffectEntry> getFoodEffects() {
            return this.foodEffects;
        }

        public int getMaxStackSize() {
            return maxStackSize;
        }

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

            public Builder nutrition(int nutrition) {
                this.nutrition = nutrition;
                return this;
            }

            public Builder saturation(float saturation) {
                this.saturation = saturation;
                return this;
            }

            public Builder alwaysEdible() {
                this.alwaysEdible = true;
                return this;
            }

            public Builder bowlContainer() {
                this.bowlContainer = true;
                return this;
            }

            public Builder effect(@Nullable ResourceLocation effectId, int duration, int amplifier, float chance) {
                if (effectId != null) {
                    this.foodEffects.add(new ItemFoodTooltip.FoodEffectEntry(effectId, duration, amplifier, chance));
                }
                return this;
            }

            public Builder effect(ItemFoodTooltip.FoodEffectEntry effectEntry) {
                if (effectEntry != null) {
                    this.foodEffects.add(effectEntry);
                }
                return this;
            }

            public Builder effects(List<ItemFoodTooltip.FoodEffectEntry> effectEntries) {
                this.foodEffects.clear();
                if (effectEntries != null) {
                    this.foodEffects.addAll(effectEntries);
                }
                return this;
            }

            public Builder maxStackSize(int maxStackSize) {
                this.maxStackSize = maxStackSize;
                return this;
            }

            public Builder extraTooltipKeys(String... extraTooltipKeys) {
                this.extraTooltipKeys = extraTooltipKeys == null ? new String[0] : extraTooltipKeys.clone();
                return this;
            }

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

        public static Builder builder() {
            return new Builder();
        }

        public int getNutrition() {
            return nutrition;
        }

        public float getSaturation() {
            return saturation;
        }

        public boolean isAlwaysEdible() {
            return alwaysEdible;
        }

        public List<ItemFoodTooltip.FoodEffectEntry> getFoodEffects() {
            return this.foodEffects;
        }

        public ItemDrinkableTooltip.DrinkEffect getDrinkEffect() {
            return drinkEffect;
        }

        public ItemStack getContainerItem() {
            return this.containerItem.copy();
        }

        public int getMaxStackSize() {
            return maxStackSize;
        }

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

            public Builder nutrition(int nutrition) {
                this.nutrition = nutrition;
                return this;
            }

            public Builder saturation(float saturation) {
                this.saturation = saturation;
                return this;
            }

            public Builder alwaysEdible() {
                this.alwaysEdible = true;
                return this;
            }

            public Builder effect(@Nullable ResourceLocation effectId, int duration, int amplifier, float chance) {
                if (effectId != null) {
                    this.foodEffects.add(new ItemFoodTooltip.FoodEffectEntry(effectId, duration, amplifier, chance));
                }
                return this;
            }

            public Builder effect(ItemFoodTooltip.FoodEffectEntry effectEntry) {
                if (effectEntry != null) {
                    this.foodEffects.add(effectEntry);
                }
                return this;
            }

            public Builder effects(List<ItemFoodTooltip.FoodEffectEntry> effectEntries) {
                this.foodEffects.clear();
                if (effectEntries != null) {
                    this.foodEffects.addAll(effectEntries);
                }
                return this;
            }

            public Builder drinkEffect(ItemDrinkableTooltip.DrinkEffect drinkEffect) {
                this.drinkEffect = drinkEffect;
                return this;
            }

            public Builder containerItem(ItemStack containerItem) {
                this.containerItem = containerItem == null ? ItemStack.EMPTY : containerItem.copy();
                return this;
            }

            public Builder maxStackSize(int maxStackSize) {
                this.maxStackSize = maxStackSize;
                return this;
            }

            public Builder extraTooltipKeys(String... extraTooltipKeys) {
                this.extraTooltipKeys = extraTooltipKeys == null ? new String[0] : extraTooltipKeys.clone();
                return this;
            }

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

        public static Builder builder() {
            return new Builder();
        }

        public int getMaxServings() {
            return maxServings;
        }

        public String getServingItemPath() {
            return servingItemPath;
        }

        @Nullable
        public String getRequiredContainerPath() {
            return requiredContainerPath;
        }

        public boolean hasLeftovers() {
            return hasLeftovers;
        }

        public float getHardness() {
            return hardness;
        }

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

            public Builder maxServings(int maxServings) {
                this.maxServings = maxServings;
                return this;
            }

            public Builder servingItem(String servingItemPath) {
                this.servingItemPath = servingItemPath;
                return this;
            }

            public Builder requiredContainer(@Nullable String requiredContainerPath) {
                this.requiredContainerPath = requiredContainerPath;
                return this;
            }

            public Builder noRequiredContainer() {
                this.requiredContainerPath = null;
                return this;
            }

            public Builder hasLeftovers(boolean hasLeftovers) {
                this.hasLeftovers = hasLeftovers;
                return this;
            }

            public Builder strength(float hardness, float resistance) {
                this.hardness = hardness;
                this.resistance = resistance;
                return this;
            }

            public PlaceableFoodSettings build() {
                return new PlaceableFoodSettings(this);
            }
        }
    }

    public static class PlaceableFoodBlock extends AddonPlaceableFoodBlock {

        public PlaceableFoodBlock(FoodItemApi.PlaceableFoodSettings settings) {
            super(settings);
        }
    }
}
