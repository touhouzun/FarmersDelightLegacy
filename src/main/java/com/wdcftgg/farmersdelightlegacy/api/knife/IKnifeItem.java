package com.wdcftgg.farmersdelightlegacy.api.knife;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Marker interface for items that should behave as Farmer's Delight knives.
 *
 * <p>Items implementing this interface are treated as knives by the built-in knife features,
 * including knife-based hunting drops, harvest drops, cutting board recipes, cake and pie slicing,
 * knife knockback handling, and the Hunting Drops / Harvest Drops JEI knife displays.</p>
 */
public interface IKnifeItem {

    /**
     * Called after this knife successfully processes a cutting board recipe.
     *
     * @param stack the knife stack used by the cutting board
     * @param world the world containing the cutting board
     * @param player the player using the cutting board, or null when activated by automation
     */
    default void onCuttingBoardRecipeProcessed(ItemStack stack, World world, EntityPlayer player) {
    }
}
