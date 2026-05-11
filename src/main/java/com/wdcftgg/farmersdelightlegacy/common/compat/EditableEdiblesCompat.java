package com.wdcftgg.farmersdelightlegacy.common.compat;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Method;

public final class EditableEdiblesCompat {

    private static final String MOD_ID = "editableedibles";
    private static Method getFoodEffectMapMethod;
    private static boolean methodLookedUp;

    private EditableEdiblesCompat() {
    }

    public static void warmFoodEffectMap() {
        if (!Loader.isModLoaded(MOD_ID)) {
            return;
        }

        synchronized (EditableEdiblesCompat.class) {
            Method method = getFoodEffectMapMethod();
            if (method == null) {
                return;
            }

            try {
                method.invoke(null);
            } catch (ReflectiveOperationException | RuntimeException exception) {
                FarmersDelightLegacy.LOGGER.warn("Failed to warm EditableEdibles food effect map", exception);
            }
        }
    }

    private static Method getFoodEffectMapMethod() {
        if (methodLookedUp) {
            return getFoodEffectMapMethod;
        }

        methodLookedUp = true;
        try {
            Class<?> configHandlerClass = Class.forName("editableedibles.handlers.ForgeConfigHandler");
            getFoodEffectMapMethod = configHandlerClass.getMethod("getFoodEffectMap");
        } catch (ReflectiveOperationException exception) {
            FarmersDelightLegacy.LOGGER.warn("EditableEdibles ForgeConfigHandler#getFoodEffectMap was not found", exception);
        }
        return getFoodEffectMapMethod;
    }
}
