package com.wdcftgg.farmersdelightlegacy.common.compat.crafttweaker;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.api.recipe.knife.HuntingDropOutput;
import com.wdcftgg.farmersdelightlegacy.common.recipe.manager.HuntingDropRecipeManager;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;
import java.util.List;

@ZenRegister
@ZenClass("mods.farmersdelight.HuntingDrop")
public final class ZenHuntingDropRecipes {

    private ZenHuntingDropRecipes() {
    }

    @ZenMethod
    public static boolean addRecipe(String key, IEntityDefinition targetEntity, IItemStack outputStack) {
        return addRecipeAdvanced(key, targetEntity, new IItemStack[]{outputStack}, null, null, false, false);
    }

    @ZenMethod
    public static boolean addRecipeAdvanced(String key, IEntityDefinition targetEntity, IItemStack[] outputStacks,
                                            float[] chances, float[] lootingBonuses,
                                            boolean burningRequired, boolean preventDuplicateStacking) {
        return registerRecipe(key, targetEntity, outputStacks, chances, lootingBonuses, burningRequired,
                preventDuplicateStacking, false);
    }

    @ZenMethod
    public static boolean addJeiRecipe(String key, IEntityDefinition targetEntity, IItemStack outputStack) {
        return addJeiRecipeAdvanced(key, targetEntity, new IItemStack[]{outputStack}, null, null, false, false);
    }

    @ZenMethod
    public static boolean addJeiRecipeAdvanced(String key, IEntityDefinition targetEntity, IItemStack[] outputStacks,
                                               float[] chances, float[] lootingBonuses,
                                               boolean burningRequired, boolean preventDuplicateStacking) {
        return registerRecipe(key, targetEntity, outputStacks, chances, lootingBonuses, burningRequired,
                preventDuplicateStacking, true);
    }

    @ZenMethod
    public static boolean addRecipeWithEntityConfigurator(String key, IEntityDefinition targetEntity,
                                                          IItemStack outputStack,
                                                          ZenHuntingDropEntityConfigurator entityConfigurator) {
        return addRecipeAdvancedWithEntityConfigurator(key, targetEntity, new IItemStack[]{outputStack}, null, null,
                false, entityConfigurator, false, null);
    }

    @ZenMethod
    public static boolean addRecipeAdvancedWithEntityConfigurator(String key, IEntityDefinition targetEntity,
                                                                  IItemStack[] outputStacks, float[] chances,
                                                                  float[] lootingBonuses, boolean preventDuplicateStacking,
                                                                  ZenHuntingDropEntityConfigurator entityConfigurator,
                                                                  boolean configureEveryTick, String[] jeiDisplayTexts) {
        return registerConfiguredRecipe(key, targetEntity, outputStacks, chances, lootingBonuses,
                preventDuplicateStacking, entityConfigurator, configureEveryTick, jeiDisplayTexts, false);
    }

    @ZenMethod
    public static boolean addJeiRecipeWithEntityConfigurator(String key, IEntityDefinition targetEntity,
                                                             IItemStack outputStack,
                                                             ZenHuntingDropEntityConfigurator entityConfigurator) {
        return addJeiRecipeAdvancedWithEntityConfigurator(key, targetEntity, new IItemStack[]{outputStack}, null, null,
                false, entityConfigurator, false, null);
    }

    @ZenMethod
    public static boolean addJeiRecipeAdvancedWithEntityConfigurator(String key, IEntityDefinition targetEntity,
                                                                     IItemStack[] outputStacks, float[] chances,
                                                                     float[] lootingBonuses, boolean preventDuplicateStacking,
                                                                     ZenHuntingDropEntityConfigurator entityConfigurator,
                                                                     boolean configureEveryTick, String[] jeiDisplayTexts) {
        return registerConfiguredRecipe(key, targetEntity, outputStacks, chances, lootingBonuses,
                preventDuplicateStacking, entityConfigurator, configureEveryTick, jeiDisplayTexts, true);
    }

    @ZenMethod
    public static boolean removeRecipe(String key) {
        return HuntingDropRecipeManager.unregisterRecipe(key);
    }

    private static boolean registerRecipe(String key, IEntityDefinition targetEntity, IItemStack[] outputStacks,
                                          float[] chances, float[] lootingBonuses,
                                          boolean burningRequired, boolean preventDuplicateStacking, boolean jeiOnly) {
        ResourceLocation entityLocation = getEntityLocation(targetEntity);
        List<HuntingDropOutput> outputs = CraftTweakerCompatHelper.toDropOutputs(outputStacks, chances, lootingBonuses);
        if (entityLocation == null || outputs == null) {
            return false;
        }

        HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher = target -> matchesEntity(target, entityLocation);
        if (jeiOnly) {
            return HuntingDropRecipeManager.registerRecipeJei(key, targetMatcher, outputs, burningRequired,
                    preventDuplicateStacking, entityLocation);
        }
        return HuntingDropRecipeManager.registerRecipe(key, targetMatcher, outputs, burningRequired,
                preventDuplicateStacking, entityLocation);
    }

    private static boolean registerConfiguredRecipe(String key, IEntityDefinition targetEntity, IItemStack[] outputStacks,
                                                     float[] chances, float[] lootingBonuses,
                                                     boolean preventDuplicateStacking,
                                                     ZenHuntingDropEntityConfigurator entityConfigurator,
                                                     boolean configureEveryTick, String[] jeiDisplayTexts, boolean jeiOnly) {
        ResourceLocation entityLocation = getEntityLocation(targetEntity);
        List<HuntingDropOutput> outputs = CraftTweakerCompatHelper.toDropOutputs(outputStacks, chances, lootingBonuses);
        if (entityLocation == null || outputs == null || entityConfigurator == null) {
            return false;
        }

        HuntingDropRecipeManager.HuntingTargetMatcher targetMatcher = target -> matchesEntity(target, entityLocation);
        List<String> displayTexts = jeiDisplayTexts == null ? null : Arrays.asList(jeiDisplayTexts);
        if (jeiOnly) {
            return HuntingDropRecipeManager.registerRecipeJei(key, targetMatcher, outputs, preventDuplicateStacking,
                    entityLocation, entity -> configurePreviewEntity(key, entityConfigurator, entity), configureEveryTick,
                    displayTexts);
        }
        return HuntingDropRecipeManager.registerRecipe(key, targetMatcher, outputs, preventDuplicateStacking,
                entityLocation, entity -> configurePreviewEntity(key, entityConfigurator, entity), configureEveryTick,
                displayTexts);
    }

    private static ResourceLocation getEntityLocation(IEntityDefinition targetEntity) {
        if (targetEntity == null || targetEntity.getId() == null || targetEntity.getId().isEmpty()) {
            return null;
        }
        try {
            ResourceLocation entityLocation = new ResourceLocation(targetEntity.getId());
            return EntityList.getClass(entityLocation) == null ? null : entityLocation;
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static boolean matchesEntity(EntityLivingBase target, ResourceLocation entityLocation) {
        return entityLocation.equals(EntityList.getKey(target));
    }

    private static void configurePreviewEntity(String key, ZenHuntingDropEntityConfigurator entityConfigurator,
                                               EntityLivingBase entity) {
        try {
            entityConfigurator.configure(CraftTweakerMC.getIEntity(entity));
        } catch (Throwable throwable) {
            FarmersDelightLegacy.LOGGER.error("CraftTweaker hunting drop entity configurator failed: {}", key, throwable);
        }
    }
}
