package com.wdcftgg.farmersdelightlegacy.api.knife;

import com.wdcftgg.farmersdelightlegacy.common.recipe.KnifeItemManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Public API for customizing knife eligibility and JEI knife displays.
 *
 * <p>Knife eligibility controls the Hunting Drops and Harvest Drops features. JEI display stacks
 * only control the displayed catalysts and recipe tools, without changing gameplay eligibility.</p>
 */
public final class KnifeItemApi {

    private KnifeItemApi() {
    }

    /**
     * Adds a stack to the knife set used by hunting drops, harvest drops, and automatic knife JEI displays.
     *
     * @param knifeStack the item and metadata to recognize as a knife; wildcard metadata matches every variant
     * @return true when the knife set changed
     */
    public static boolean addKnife(ItemStack knifeStack) {
        return KnifeItemManager.addKnife(knifeStack);
    }

    /**
     * Removes a stack from the knife set used by hunting drops, harvest drops, and automatic knife JEI displays.
     *
     * @param knifeStack the item and metadata to stop recognizing as a knife; wildcard metadata matches every variant
     * @return true when the knife set changed
     */
    public static boolean removeKnife(ItemStack knifeStack) {
        return KnifeItemManager.removeKnife(knifeStack);
    }

    /**
     * Removes every stack whose item is an instance of the supplied item's runtime class from the knife set
     * used by hunting drops, harvest drops, and automatic knife JEI displays.
     *
     * @param knifeItem the item to stop recognizing as a knife
     * @return true when the knife set changed
     */
    public static boolean removeKnife(Item knifeItem) {
        return KnifeItemManager.removeKnife(knifeItem);
    }

    /**
     * Adds a stack to the Hunting Drops and Harvest Drops JEI displays without changing drop eligibility.
     *
     * @param displayStack the stack to show in JEI
     * @return true when the JEI display set changed
     */
    public static boolean addJeiDisplayStack(ItemStack displayStack) {
        return KnifeItemManager.addJeiDisplayStack(displayStack);
    }

    /**
     * Removes a stack from automatic Hunting Drops and Harvest Drops JEI displays without changing drop eligibility.
     *
     * @param displayStack the item and metadata to hide from JEI; wildcard metadata matches every variant
     * @return true when the JEI display set changed
     */
    public static boolean removeJeiDisplayStack(ItemStack displayStack) {
        return KnifeItemManager.removeJeiDisplayStack(displayStack);
    }

    /**
     * Removes every stack whose item is an instance of the supplied item's runtime class from the left-hand
     * knife input of Hunting Drops and Harvest Drops JEI recipes without changing drop eligibility.
     *
     * @param displayItem the item to hide from JEI
     * @return true when the JEI display set changed
     */
    public static boolean removeJeiDisplayItem(Item displayItem) {
        return KnifeItemManager.removeJeiDisplayItem(displayItem);
    }

    /**
     * Clears all explicit knife additions and removals, restoring built-in knife recognition.
     */
    public static void clearKnifeOverrides() {
        KnifeItemManager.clearKnifeOverrides();
    }

    /**
     * Clears all explicit JEI display additions and removals, restoring automatic knife displays.
     */
    public static void clearJeiDisplayOverrides() {
        KnifeItemManager.clearJeiDisplayOverrides();
    }

    /**
     * Gets representative stacks for every knife currently eligible to trigger Hunting Drops and Harvest Drops.
     *
     * @return an immutable snapshot of gameplay-eligible knife stacks
     */
    public static List<ItemStack> getHuntingAndHarvestKnifeStacks() {
        return KnifeItemManager.getHuntingAndHarvestKnifeStacks();
    }

    /**
     * Gets the stacks currently shown in the left-hand knife input of Hunting Drops and Harvest Drops JEI recipes.
     *
     * <p>This is the same list used for the categories' recipe catalysts. It does not return
     * standalone JEI ingredient information entries.</p>
     *
     * @return an immutable snapshot of JEI display stacks
     */
    public static List<ItemStack> getJeiDisplayStacks() {
        return KnifeItemManager.getJeiDisplayStacks();
    }
}
