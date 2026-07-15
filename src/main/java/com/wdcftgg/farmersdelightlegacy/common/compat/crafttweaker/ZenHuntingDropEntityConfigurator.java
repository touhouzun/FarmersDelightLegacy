package com.wdcftgg.farmersdelightlegacy.common.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.farmersdelight.function.HuntingDropEntityConfigurator")
@FunctionalInterface
public interface ZenHuntingDropEntityConfigurator {

    @ZenMethod
    void configure(IEntity entity);
}
