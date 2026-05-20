package com.wdcftgg.farmersdelightlegacy.common.util;

import com.google.common.collect.ImmutableList;
import com.wdcftgg.farmersdelightlegacy.api.knife.IKnifeItem;
import com.wdcftgg.farmersdelightlegacy.common.item.ItemKnife;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class KnifeItemStacks {

    private KnifeItemStacks() {
    }

    public static List<ItemStack> getJeiDisplayStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
            ItemStack stack = new ItemStack(item);
            if (!ItemKnife.isKnife(stack)) {
                continue;
            }
            ItemStack displayStack = getJeiDisplayStack(stack);
            if (!displayStack.isEmpty()) {
                stacks.add(displayStack);
            }
        }
        return stacks.isEmpty() ? Collections.emptyList() : ImmutableList.copyOf(stacks);
    }

    public static ItemStack getJeiDisplayStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        Item item = stack.getItem();
        if (item instanceof IKnifeItem) {
            ItemStack displayStack = ((IKnifeItem) item).getKnifeJeiInfoStack(stack);
            return displayStack.isEmpty() ? stack.copy() : displayStack;
        }
        return stack.copy();
    }
}
