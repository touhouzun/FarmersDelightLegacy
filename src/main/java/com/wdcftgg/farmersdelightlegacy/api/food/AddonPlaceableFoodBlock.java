package com.wdcftgg.farmersdelightlegacy.api.food;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockFeast;

import javax.annotation.Nullable;

/**
 * Public base block for addon feast-style placeable foods.
 */
public class AddonPlaceableFoodBlock extends BlockFeast {

    public AddonPlaceableFoodBlock(int maxServings, String servingItemPath, @Nullable String requiredContainerPath) {
        super(maxServings, servingItemPath, requiredContainerPath);
    }

    public AddonPlaceableFoodBlock(int maxServings, String servingItemPath, @Nullable String requiredContainerPath,
                                   boolean hasLeftovers) {
        super(maxServings, servingItemPath, requiredContainerPath, hasLeftovers);
    }

    public AddonPlaceableFoodBlock(FoodItemApi.PlaceableFoodSettings settings) {
        super(normalize(settings).getMaxServings(), normalize(settings).getServingItemPath(),
                normalize(settings).getRequiredContainerPath(), normalize(settings).hasLeftovers());
        FoodItemApi.PlaceableFoodSettings value = normalize(settings);
        this.setHardness(value.getHardness());
        this.setResistance(value.getResistance());
    }

    private static FoodItemApi.PlaceableFoodSettings normalize(FoodItemApi.PlaceableFoodSettings settings) {
        return settings == null ? FoodItemApi.PlaceableFoodSettings.builder().build() : settings;
    }
}