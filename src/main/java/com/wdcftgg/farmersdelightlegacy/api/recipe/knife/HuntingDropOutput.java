package com.wdcftgg.farmersdelightlegacy.api.recipe.knife;

import net.minecraft.item.ItemStack;

/**
 * Hunting drop output entry with its own chance and Looting bonus.
 */
public final class HuntingDropOutput {

    private final ItemStack outputStack;
    private final float chance;
    private final float lootingBonus;

    /**
     * Creates a hunting drop output entry.
     *
     * @param outputStack The output stack to register, remove, match, or display.
     */
    public HuntingDropOutput(ItemStack outputStack) {
        this(outputStack, 1.0F, 0.0F);
    }

    /**
     * Creates a hunting drop output entry.
     *
     * @param outputStack The output stack to register, remove, match, or display.
     * @param chance The base chance for this output or effect, using the 0.0 to 1.0 range.
     * @param lootingBonus The additional drop chance added per Looting level.
     */
    public HuntingDropOutput(ItemStack outputStack, float chance, float lootingBonus) {
        this.outputStack = outputStack == null ? ItemStack.EMPTY : copyOneOrMore(outputStack);
        this.chance = clampChance(chance);
        this.lootingBonus = Math.max(0.0F, lootingBonus);
    }

    /**
     * Creates a hunting drop output entry.
     *
     * @param outputStack The output stack to register, remove, match, or display.
     * @return The result produced by this API method.
     */
    public static HuntingDropOutput of(ItemStack outputStack) {
        return new HuntingDropOutput(outputStack);
    }

    /**
     * Creates a hunting drop output entry.
     *
     * @param outputStack The output stack to register, remove, match, or display.
     * @param chance The base chance for this output or effect, using the 0.0 to 1.0 range.
     * @param lootingBonus The additional drop chance added per Looting level.
     * @return The result produced by this API method.
     */
    public static HuntingDropOutput of(ItemStack outputStack, float chance, float lootingBonus) {
        return new HuntingDropOutput(outputStack, chance, lootingBonus);
    }

    /**
     * Returns a copy of the output stack.
     *
     * @return The result produced by this API method.
     */
    public ItemStack getOutputStack() {
        return this.outputStack.copy();
    }

    /**
     * Returns the base drop chance.
     *
     * @return The result produced by this API method.
     */
    public float getChance() {
        return this.chance;
    }

    /**
     * Returns the Looting bonus per level.
     *
     * @return The result produced by this API method.
     */
    public float getLootingBonus() {
        return this.lootingBonus;
    }

    /**
     * Calculates the drop chance with Looting applied.
     *
     * @param lootingLevel The Looting level used to calculate the final chance.
     * @return The result produced by this API method.
     */
    public float getChanceWithLooting(int lootingLevel) {
        return clampChance(this.chance + Math.max(0, lootingLevel) * this.lootingBonus);
    }

    /**
     * Returns whether this output entry has no stack to drop.
     *
     * @return The result produced by this API method.
     */
    public boolean isEmpty() {
        return this.outputStack.isEmpty();
    }

    private static ItemStack copyOneOrMore(ItemStack stack) {
        ItemStack copiedStack = stack.copy();
        copiedStack.setCount(Math.max(1, copiedStack.getCount()));
        return copiedStack;
    }

    private static float clampChance(float chance) {
        if (chance < 0.0F) {
            return 0.0F;
        }
        return Math.min(1.0F, chance);
    }
}
