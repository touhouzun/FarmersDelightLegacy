package com.wdcftgg.farmersdelightlegacy.api.food;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockFeast;

import javax.annotation.Nullable;

/**
 * Public base block for addon feast-style placeable foods.
 */
public class AddonPlaceableFoodBlock extends BlockFeast {

    /**
     * Creates a placeable multi-serving food block.
     *
     * @param maxServings The maximum number of servings the block can provide.
     * @param servingItemPath The registry name string of the item given for each serving.
     * @param requiredContainerPath The registry name string of the required serving container; {@code null} means no container is required.
     */
    public AddonPlaceableFoodBlock(int maxServings, String servingItemPath, @Nullable String requiredContainerPath) {
        super(maxServings, servingItemPath, requiredContainerPath);
    }

    /**
     * Creates a placeable multi-serving food block.
     *
     * @param maxServings The maximum number of servings the block can provide.
     * @param servingItemPath The registry name string of the item given for each serving.
     * @param requiredContainerPath The registry name string of the required serving container; {@code null} means no container is required.
     * @param hasLeftovers Whether the block keeps a leftover stage after the final serving is taken.
     */
    public AddonPlaceableFoodBlock(int maxServings, String servingItemPath, @Nullable String requiredContainerPath,
                                   boolean hasLeftovers) {
        super(maxServings, servingItemPath, requiredContainerPath, hasLeftovers);
    }

    /**
     * Creates a placeable multi-serving food block.
     *
     * @param settings The settings object used to configure the created item or block; {@code null} uses defaults when supported.
     */
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
