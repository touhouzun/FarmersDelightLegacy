package com.wdcftgg.farmersdelightlegacy.client.jei;

import com.wdcftgg.farmersdelightlegacy.common.inventory.ContainerCookingPot;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public final class CookingPotRecipeTransferInfo implements IRecipeTransferInfo<ContainerCookingPot> {

    @Override
    public Class<ContainerCookingPot> getContainerClass() {
        return ContainerCookingPot.class;
    }

    @Override
    public String getRecipeCategoryUid() {
        return JeiUids.COOKING_POT;
    }

    @Override
    public boolean canHandle(ContainerCookingPot container) {
        return true;
    }

    @Override
    public List<Slot> getRecipeSlots(ContainerCookingPot container) {
        List<Slot> recipeSlots = new ArrayList<>(ContainerCookingPot.INPUT_SLOT_COUNT + 1);
        recipeSlots.addAll(container.inventorySlots.subList(0, ContainerCookingPot.INPUT_SLOT_COUNT));
        recipeSlots.add(container.inventorySlots.get(ContainerCookingPot.CONTAINER_SLOT));
        return recipeSlots;
    }

    @Override
    public List<Slot> getInventorySlots(ContainerCookingPot container) {
        int inventoryStartSlot = ContainerCookingPot.PLAYER_INVENTORY_START_SLOT;
        int inventoryEndSlot = inventoryStartSlot + ContainerCookingPot.PLAYER_INVENTORY_SLOT_COUNT;
        return new ArrayList<>(container.inventorySlots.subList(inventoryStartSlot, inventoryEndSlot));
    }
}
