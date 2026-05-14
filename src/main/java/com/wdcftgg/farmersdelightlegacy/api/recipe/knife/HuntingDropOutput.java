package com.wdcftgg.farmersdelightlegacy.api.recipe.knife;

import net.minecraft.item.ItemStack;

/**
 * Hunting drop output entry with its own chance and Looting bonus.
 */
public final class HuntingDropOutput {

    private final ItemStack outputStack;
    private final float chance;
    private final float lootingBonus;

    public HuntingDropOutput(ItemStack outputStack) {
        this(outputStack, 1.0F, 0.0F);
    }

    public HuntingDropOutput(ItemStack outputStack, float chance, float lootingBonus) {
        this.outputStack = outputStack == null ? ItemStack.EMPTY : copyOneOrMore(outputStack);
        this.chance = clampChance(chance);
        this.lootingBonus = Math.max(0.0F, lootingBonus);
    }

    public static HuntingDropOutput of(ItemStack outputStack) {
        return new HuntingDropOutput(outputStack);
    }

    public static HuntingDropOutput of(ItemStack outputStack, float chance, float lootingBonus) {
        return new HuntingDropOutput(outputStack, chance, lootingBonus);
    }

    public ItemStack getOutputStack() {
        return this.outputStack.copy();
    }

    public float getChance() {
        return this.chance;
    }

    public float getLootingBonus() {
        return this.lootingBonus;
    }

    public float getChanceWithLooting(int lootingLevel) {
        return clampChance(this.chance + Math.max(0, lootingLevel) * this.lootingBonus);
    }

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