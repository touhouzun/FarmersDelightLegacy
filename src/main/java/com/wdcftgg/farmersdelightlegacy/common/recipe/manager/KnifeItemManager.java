package com.wdcftgg.farmersdelightlegacy.common.recipe.manager;

import com.wdcftgg.farmersdelightlegacy.api.knife.IKnifeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the item stacks recognized as Farmer's Delight knives and their JEI displays.
 *
 * <p>Built-in knife recognition remains enabled for {@link IKnifeItem} implementations,
 * {@code toolKnife} OreDictionary entries, and registered item paths ending in {@code _knife}.
 * Explicitly added knife stacks take priority over removals, allowing a specific metadata value
 * to be restored after a wildcard removal.</p>
 */
public final class KnifeItemManager {

    private static final List<KnifeStackMatcher> addedKnifeMatchers = new ArrayList<>();
    private static final List<KnifeStackMatcher> removedKnifeMatchers = new ArrayList<>();
    private static final List<Class<? extends Item>> removedKnifeItemClasses = new ArrayList<>();
    private static final List<ItemStack> addedJeiDisplayStacks = new ArrayList<>();
    private static final List<KnifeStackMatcher> removedJeiDisplayMatchers = new ArrayList<>();
    private static final List<Class<? extends Item>> removedJeiDisplayItemClasses = new ArrayList<>();

    private KnifeItemManager() {
    }

    /**
     * Adds a stack to the knife set used by hunting drops, harvest drops, and knife JEI displays.
     *
     * @param knifeStack the item and metadata to recognize as a knife; wildcard metadata matches every variant
     * @return true when the knife set changed
     */
    public static synchronized boolean addKnife(ItemStack knifeStack) {
        KnifeStackMatcher matcher = KnifeStackMatcher.fromStack(knifeStack);
        if (matcher == null || containsMatcher(addedKnifeMatchers, matcher)) {
            return false;
        }
        addedKnifeMatchers.add(matcher);
        return true;
    }

    /**
     * Removes a stack from the knife set used by hunting drops, harvest drops, and automatic knife JEI displays.
     *
     * @param knifeStack the item and metadata to stop recognizing as a knife; wildcard metadata matches every variant
     * @return true when the knife set changed
     */
    public static synchronized boolean removeKnife(ItemStack knifeStack) {
        KnifeStackMatcher matcher = KnifeStackMatcher.fromStack(knifeStack);
        if (matcher == null) {
            return false;
        }

        boolean changed = removeMatchingMatchers(addedKnifeMatchers, matcher);
        if (!containsMatcher(removedKnifeMatchers, matcher)) {
            removedKnifeMatchers.add(matcher);
            changed = true;
        }
        return changed;
    }

    /**
     * Removes every stack whose item is an instance of the supplied item's runtime class from the knife set
     * used by hunting drops, harvest drops, and automatic knife JEI displays.
     *
     * @param knifeItem the item to stop recognizing as a knife
     * @return true when the knife set changed
     */
    public static synchronized boolean removeKnife(Item knifeItem) {
        if (knifeItem == null) {
            return false;
        }

        Class<? extends Item> knifeItemClass = knifeItem.getClass();
        boolean changed = removeMatchingMatchers(addedKnifeMatchers, knifeItemClass);
        if (!removedKnifeItemClasses.contains(knifeItemClass)) {
            removedKnifeItemClasses.add(knifeItemClass);
            changed = true;
        }
        return changed;
    }

    /**
     * Adds a stack to the Hunting Drops and Harvest Drops JEI displays without changing drop eligibility.
     *
     * @param displayStack the stack to show in JEI
     * @return true when the JEI display set changed
     */
    public static synchronized boolean addJeiDisplayStack(ItemStack displayStack) {
        if (displayStack.isEmpty()) {
            return false;
        }

        ItemStack copiedStack = copyDisplayStack(displayStack);
        if (containsDisplayStack(addedJeiDisplayStacks, copiedStack)) {
            return false;
        }
        addedJeiDisplayStacks.add(copiedStack);
        return true;
    }

    /**
     * Removes a stack from automatic Hunting Drops and Harvest Drops JEI displays without changing drop eligibility.
     *
     * @param displayStack the item and metadata to hide from JEI; wildcard metadata matches every variant
     * @return true when the JEI display set changed
     */
    public static synchronized boolean removeJeiDisplayStack(ItemStack displayStack) {
        KnifeStackMatcher matcher = KnifeStackMatcher.fromStack(displayStack);
        if (matcher == null) {
            return false;
        }

        boolean changed = removeMatchingDisplayStacks(addedJeiDisplayStacks, matcher);
        if (!containsMatcher(removedJeiDisplayMatchers, matcher)) {
            removedJeiDisplayMatchers.add(matcher);
            changed = true;
        }
        return changed;
    }

    /**
     * Removes every stack whose item is an instance of the supplied item's runtime class from automatic
     * Hunting Drops and Harvest Drops JEI displays without changing drop eligibility.
     *
     * @param displayItem the item to hide from JEI
     * @return true when the JEI display set changed
     */
    public static synchronized boolean removeJeiDisplayItem(Item displayItem) {
        if (displayItem == null) {
            return false;
        }

        Class<? extends Item> displayItemClass = displayItem.getClass();
        boolean changed = removeMatchingDisplayStacks(addedJeiDisplayStacks, displayItemClass);
        if (!removedJeiDisplayItemClasses.contains(displayItemClass)) {
            removedJeiDisplayItemClasses.add(displayItemClass);
            changed = true;
        }
        return changed;
    }

    /**
     * Clears all explicit knife additions and removals, restoring built-in knife recognition.
     */
    public static synchronized void clearKnifeOverrides() {
        addedKnifeMatchers.clear();
        removedKnifeMatchers.clear();
        removedKnifeItemClasses.clear();
    }

    /**
     * Clears all explicit JEI display additions and removals, restoring automatic knife displays.
     */
    public static synchronized void clearJeiDisplayOverrides() {
        addedJeiDisplayStacks.clear();
        removedJeiDisplayMatchers.clear();
        removedJeiDisplayItemClasses.clear();
    }

    /**
     * Checks whether a stack is currently recognized as a Farmer's Delight knife.
     *
     * @param stack the stack to check
     * @return true when the stack can trigger knife hunting and harvest drops
     */
    public static synchronized boolean isKnife(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (matchesAny(addedKnifeMatchers, stack)) {
            return true;
        }
        if (matchesAny(removedKnifeMatchers, stack) || matchesAnyItemClass(removedKnifeItemClasses, stack)) {
            return false;
        }
        return isBuiltInKnife(stack);
    }

    /**
     * Gets representative stacks for every knife currently eligible to trigger Hunting Drops and Harvest Drops.
     *
     * <p>This snapshot includes explicit knife additions and automatically recognized knife entries, but excludes
     * JEI-only display additions.</p>
     *
     * @return an immutable snapshot of gameplay-eligible knife stacks
     */
    public static synchronized List<ItemStack> getHuntingAndHarvestKnifeStacks() {
        List<ItemStack> knifeStacks = new ArrayList<>();
        for (KnifeStackMatcher matcher : addedKnifeMatchers) {
            addUniqueDisplayStack(knifeStacks, matcher.toDisplayStack());
        }

        for (ItemStack oreStack : OreDictionary.getOres("toolKnife")) {
            if (isKnife(oreStack)) {
                addUniqueDisplayStack(knifeStacks, getRepresentativeStack(oreStack));
            }
        }

        for (Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
            ItemStack knifeStack = new ItemStack(item);
            if (isKnife(knifeStack)) {
                addUniqueDisplayStack(knifeStacks, knifeStack);
            }
        }
        return knifeStacks.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(knifeStacks);
    }

    /**
     * Gets the stacks shown in the left-hand knife input of Hunting Drops and Harvest Drops JEI recipes.
     *
     * <p>This is the same list used for the categories' recipe catalysts. It does not return
     * standalone JEI ingredient information entries.</p>
     *
     * @return an immutable snapshot of JEI display stacks
     */
    public static synchronized List<ItemStack> getJeiDisplayStacks() {
        List<ItemStack> displayStacks = new ArrayList<>();
        for (ItemStack displayStack : addedJeiDisplayStacks) {
            addUniqueDisplayStack(displayStacks, displayStack);
        }

        for (KnifeStackMatcher matcher : addedKnifeMatchers) {
            ItemStack displayStack = getJeiDisplayStack(matcher.toDisplayStack());
            if (!displayStack.isEmpty() && !isJeiDisplayRemoved(displayStack)) {
                addUniqueDisplayStack(displayStacks, displayStack);
            }
        }

        for (Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
            ItemStack knifeStack = new ItemStack(item);
            if (!isKnife(knifeStack)) {
                continue;
            }

            ItemStack displayStack = getJeiDisplayStack(knifeStack);
            if (!displayStack.isEmpty() && !isJeiDisplayRemoved(displayStack)) {
                addUniqueDisplayStack(displayStacks, displayStack);
            }
        }
        return displayStacks.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(displayStacks);
    }

    /**
     * Gets the default JEI display stack for a recognized knife.
     *
     * @param knifeStack the recognized knife stack
     * @return the configured display stack, or an empty stack when the input is empty
     */
    public static ItemStack getJeiDisplayStack(ItemStack knifeStack) {
        if (knifeStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        Item item = knifeStack.getItem();
        if (item instanceof IKnifeItem) {
            ItemStack displayStack = ((IKnifeItem) item).getKnifeJeiInfoStack(knifeStack);
            return displayStack.isEmpty() ? copyDisplayStack(knifeStack) : copyDisplayStack(displayStack);
        }
        return copyDisplayStack(knifeStack);
    }

    private static boolean isBuiltInKnife(ItemStack stack) {
        if (stack.getItem() instanceof IKnifeItem) {
            return true;
        }

        for (ItemStack oreStack : OreDictionary.getOres("toolKnife")) {
            if (oreStack.isEmpty() || oreStack.getItem() != stack.getItem()) {
                continue;
            }

            int oreMetadata = oreStack.getMetadata();
            if (oreMetadata == OreDictionary.WILDCARD_VALUE || oreMetadata == stack.getMetadata()) {
                return true;
            }
        }

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return itemId != null && itemId.getPath().endsWith("_knife");
    }

    private static boolean containsMatcher(List<KnifeStackMatcher> matchers, KnifeStackMatcher matcher) {
        for (KnifeStackMatcher existingMatcher : matchers) {
            if (existingMatcher.equals(matcher)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesAny(List<KnifeStackMatcher> matchers, ItemStack stack) {
        for (KnifeStackMatcher matcher : matchers) {
            if (matcher.matches(stack)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesAnyItemClass(List<Class<? extends Item>> itemClasses, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();
        for (Class<? extends Item> itemClass : itemClasses) {
            if (itemClass.isInstance(item)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isJeiDisplayRemoved(ItemStack displayStack) {
        return matchesAny(removedJeiDisplayMatchers, displayStack)
                || matchesAnyItemClass(removedJeiDisplayItemClasses, displayStack);
    }

    private static boolean removeMatchingMatchers(List<KnifeStackMatcher> matchers, KnifeStackMatcher matcher) {
        boolean changed = false;
        for (int index = matchers.size() - 1; index >= 0; index--) {
            if (matcher.matches(matchers.get(index).toStack())) {
                matchers.remove(index);
                changed = true;
            }
        }
        return changed;
    }

    private static boolean removeMatchingMatchers(List<KnifeStackMatcher> matchers, Class<? extends Item> itemClass) {
        boolean changed = false;
        for (int index = matchers.size() - 1; index >= 0; index--) {
            if (itemClass.isInstance(matchers.get(index).item)) {
                matchers.remove(index);
                changed = true;
            }
        }
        return changed;
    }

    private static boolean removeMatchingDisplayStacks(List<ItemStack> displayStacks, KnifeStackMatcher matcher) {
        boolean changed = false;
        for (int index = displayStacks.size() - 1; index >= 0; index--) {
            if (matcher.matches(displayStacks.get(index))) {
                displayStacks.remove(index);
                changed = true;
            }
        }
        return changed;
    }

    private static boolean removeMatchingDisplayStacks(List<ItemStack> displayStacks, Class<? extends Item> itemClass) {
        boolean changed = false;
        for (int index = displayStacks.size() - 1; index >= 0; index--) {
            if (itemClass.isInstance(displayStacks.get(index).getItem())) {
                displayStacks.remove(index);
                changed = true;
            }
        }
        return changed;
    }

    private static void addUniqueDisplayStack(List<ItemStack> displayStacks, ItemStack displayStack) {
        if (!containsDisplayStack(displayStacks, displayStack)) {
            displayStacks.add(copyDisplayStack(displayStack));
        }
    }

    private static boolean containsDisplayStack(List<ItemStack> displayStacks, ItemStack displayStack) {
        for (ItemStack existingStack : displayStacks) {
            if (ItemStack.areItemsEqual(existingStack, displayStack)
                    && ItemStack.areItemStackTagsEqual(existingStack, displayStack)) {
                return true;
            }
        }
        return false;
    }

    private static ItemStack copyDisplayStack(ItemStack stack) {
        ItemStack copiedStack = stack.copy();
        copiedStack.setCount(1);
        return copiedStack;
    }

    private static ItemStack getRepresentativeStack(ItemStack stack) {
        ItemStack representativeStack = copyDisplayStack(stack);
        if (representativeStack.getMetadata() == OreDictionary.WILDCARD_VALUE) {
            representativeStack.setItemDamage(0);
        }
        return representativeStack;
    }

    private static final class KnifeStackMatcher {

        private final Item item;
        private final int metadata;

        private KnifeStackMatcher(Item item, int metadata) {
            this.item = item;
            this.metadata = metadata;
        }

        private static KnifeStackMatcher fromStack(ItemStack stack) {
            return stack.isEmpty() ? null : new KnifeStackMatcher(stack.getItem(), stack.getMetadata());
        }

        private boolean matches(ItemStack stack) {
            return !stack.isEmpty() && this.item == stack.getItem()
                    && (this.metadata == OreDictionary.WILDCARD_VALUE || this.metadata == stack.getMetadata());
        }

        private ItemStack toStack() {
            return new ItemStack(this.item, 1, this.metadata);
        }

        private ItemStack toDisplayStack() {
            int displayMetadata = this.metadata == OreDictionary.WILDCARD_VALUE ? 0 : this.metadata;
            return new ItemStack(this.item, 1, displayMetadata);
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof KnifeStackMatcher)) {
                return false;
            }
            KnifeStackMatcher other = (KnifeStackMatcher) object;
            return this.item == other.item && this.metadata == other.metadata;
        }

        @Override
        public int hashCode() {
            return 31 * System.identityHashCode(this.item) + this.metadata;
        }
    }
}
