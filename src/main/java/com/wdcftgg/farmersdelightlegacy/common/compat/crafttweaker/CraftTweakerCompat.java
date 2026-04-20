package com.wdcftgg.farmersdelightlegacy.common.compat.crafttweaker;

public final class CraftTweakerCompat {

    private CraftTweakerCompat() {
    }

    public static void registerAll() {
        // 保留兼容入口，不再手动注册 Zen 类，统一依赖 @ZenRegister 自动注册。
    }
}

