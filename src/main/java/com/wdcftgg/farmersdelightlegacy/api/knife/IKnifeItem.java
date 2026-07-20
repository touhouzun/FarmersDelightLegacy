package com.wdcftgg.farmersdelightlegacy.api.knife;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Marker interface for items that should behave as Farmer's Delight knives.
 *
 * <p>Items implementing this interface are treated as knives by the built-in knife features,
 * including knife-based hunting drops, harvest drops, cutting board recipes, cake and pie slicing,
 * knife knockback handling, and the Hunting Drops / Harvest Drops JEI knife displays. Use
 * {@link KnifeItemApi} to add or remove knife eligibility and JEI display stacks at runtime.</p>
 */
public interface IKnifeItem {

    /**
     * Gets the stack used when registering this knife's JEI ingredient information entry.
     *
     * <p>The default implementation returns {@link ItemStack#EMPTY}, allowing add-on knives to opt
     * in explicitly. Returning an empty stack skips the JEI info entry for this knife.</p>
     *
     * @param stack the registered knife stack being considered for JEI ingredient information
     * @return the stack that should receive the knife info entry, or {@link ItemStack#EMPTY} to skip it
     */
    default ItemStack getKnifeJeiInfoStack(ItemStack stack) {
        return ItemStack.EMPTY;
    }

    /**
     * Checks whether this knife is allowed to process the current cutting board recipe attempt.
     *
     * <p>The default implementation allows cutting. Add-on knives can override this to block
     * cutting when their own state, player, world, or cutting board context does not allow it.</p>
     *
     * @param stack the knife stack used by the cutting board
     * @param world the world containing the cutting board
     * @param pos the cutting board position
     * @param state the cutting board block state
     * @param player the player using the cutting board, or null when activated by automation
     * @param inputStack the stack currently stored on the cutting board
     * @return true when the cutting board may process the recipe, otherwise false
     */
    default boolean canProcessCuttingBoardRecipe(ItemStack stack, World world, BlockPos pos, IBlockState state,
                                                 EntityPlayer player, ItemStack inputStack) {
        return true;
    }

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
