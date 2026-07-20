package com.wdcftgg.farmersdelightlegacy.common.compat.crafttweaker;

import com.wdcftgg.farmersdelightlegacy.api.knife.KnifeItemApi;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

@ZenRegister
@ZenClass("mods.farmersdelight.Knife")
public final class ZenKnifeApi {

    private ZenKnifeApi() {
    }

    @ZenMethod
    public static boolean addKnife(IItemStack knifeStack) {
        return KnifeItemApi.addKnife(CraftTweakerCompatHelper.stackOf(knifeStack));
    }

    @ZenMethod
    public static boolean removeKnife(IItemStack knifeStack) {
        return KnifeItemApi.removeKnife(CraftTweakerCompatHelper.stackOf(knifeStack));
    }

    @ZenMethod
    public static boolean removeKnifeItem(IItemStack knifeStack) {
        ItemStack nativeStack = CraftTweakerCompatHelper.stackOf(knifeStack);
        return nativeStack.isEmpty() ? false : KnifeItemApi.removeKnife(nativeStack.getItem());
    }

    @ZenMethod
    public static boolean addJeiDisplayStack(IItemStack displayStack) {
        return KnifeItemApi.addJeiDisplayStack(CraftTweakerCompatHelper.stackOf(displayStack));
    }

    @ZenMethod
    public static boolean removeJeiDisplayStack(IItemStack displayStack) {
        return KnifeItemApi.removeJeiDisplayStack(CraftTweakerCompatHelper.stackOf(displayStack));
    }

    @ZenMethod
    public static boolean removeJeiDisplayItem(IItemStack displayStack) {
        ItemStack nativeStack = CraftTweakerCompatHelper.stackOf(displayStack);
        return nativeStack.isEmpty() ? false : KnifeItemApi.removeJeiDisplayItem(nativeStack.getItem());
    }

    @ZenMethod
    public static void clearKnifeOverrides() {
        KnifeItemApi.clearKnifeOverrides();
    }

    @ZenMethod
    public static void clearJeiDisplayOverrides() {
        KnifeItemApi.clearJeiDisplayOverrides();
    }

    @ZenMethod
    public static IItemStack[] getHuntingAndHarvestKnifeStacks() {
        return toZenStacks(KnifeItemApi.getHuntingAndHarvestKnifeStacks());
    }

    @ZenMethod
    public static IItemStack[] getJeiDisplayStacks() {
        return toZenStacks(KnifeItemApi.getJeiDisplayStacks());
    }

    private static IItemStack[] toZenStacks(List<ItemStack> nativeStacks) {
        IItemStack[] zenStacks = new IItemStack[nativeStacks.size()];
        for (int index = 0; index < nativeStacks.size(); index++) {
            zenStacks[index] = CraftTweakerMC.getIItemStack(nativeStacks.get(index));
        }
        return zenStacks;
    }
}
