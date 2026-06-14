package com.wdcftgg.farmersdelightlegacy.common.event;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

@Mod.EventBusSubscriber(modid = FarmersDelightLegacy.MOD_ID)
public final class CanvasSignFuelEventHandler {

    private static final int CANVAS_SIGN_BURN_TIME = 200;
    private static final int HANGING_CANVAS_SIGN_BURN_TIME = 800;

    private CanvasSignFuelEventHandler() {
    }

    @SubscribeEvent
    public static void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty()) {
            return;
        }

        if (hasOreName(itemStack, "fdHangingCanvasSigns")) {
            event.setBurnTime(HANGING_CANVAS_SIGN_BURN_TIME);
            return;
        }

        if (hasOreName(itemStack, "fdCanvasSigns")) {
            event.setBurnTime(CANVAS_SIGN_BURN_TIME);
        }
    }

    private static boolean hasOreName(ItemStack itemStack, String expectedOreName) {
        for (int oreId : OreDictionary.getOreIDs(itemStack)) {
            if (expectedOreName.equals(OreDictionary.getOreName(oreId))) {
                return true;
            }
        }
        return false;
    }
}
