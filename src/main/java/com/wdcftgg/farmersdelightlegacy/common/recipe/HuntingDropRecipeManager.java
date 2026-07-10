package com.wdcftgg.farmersdelightlegacy.common.recipe;

import com.google.common.collect.ImmutableList;
import com.wdcftgg.farmersdelightlegacy.api.recipe.knife.HuntingDropOutput;
import com.wdcftgg.farmersdelightlegacy.common.item.ItemKnife;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModItems;
import com.wdcftgg.farmersdelightlegacy.common.util.KnifeItemStacks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.*;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import java.util.*;
import java.util.function.Consumer;

public final class HuntingDropRecipeManager {

    private static final float HAM_DROP_CHANCE = 0.5F;
    private static final float LOOTING_BONUS = 0.1F;
    private static final Map<String, HuntingDropRecipe> RECIPES = new LinkedHashMap<>();
    private static boolean defaultsRegistered;

    private HuntingDropRecipeManager() {
    }

    public static synchronized boolean registerRecipe(String key, HuntingTargetMatcher targetMatcher, ItemStack outputStack) {
        return registerRecipe(key, targetMatcher, outputStack, false);
    }

    public static synchronized boolean registerRecipeJei(String key, HuntingTargetMatcher targetMatcher, ItemStack outputStack) {
        return registerRecipeJei(key, targetMatcher, outputStack, false);
    }

    public static synchronized boolean registerRecipe(String key, HuntingTargetMatcher targetMatcher, ItemStack outputStack,
                                                      boolean preventDuplicateStacking) {
        return registerRecipe(key, targetMatcher, outputStack, preventDuplicateStacking, 1.0F, 0.0F, false, null);
    }

    public static synchronized boolean registerRecipeJei(String key, HuntingTargetMatcher targetMatcher, ItemStack outputStack,
                                                         boolean preventDuplicateStacking) {
        return registerRecipeJei(key, targetMatcher, outputStack, preventDuplicateStacking, 1.0F, 0.0F, false, null);
    }

    public static synchronized boolean registerRecipe(String key, HuntingTargetMatcher targetMatcher, ItemStack outputStack,
                                                      boolean preventDuplicateStacking, float chance, float lootingBonus,
                                                      boolean burningRequired, ResourceLocation entityId) {
        List<HuntingDropOutput> outputs = new ArrayList<>(1);
        outputs.add(new HuntingDropOutput(outputStack, chance, lootingBonus));
        return registerRecipe(key, targetMatcher, outputs, burningRequired, preventDuplicateStacking, entityId);
    }

    public static synchronized boolean registerRecipeJei(String key, HuntingTargetMatcher targetMatcher, ItemStack outputStack,
                                                         boolean preventDuplicateStacking, float chance, float lootingBonus,
                                                         boolean burningRequired, ResourceLocation entityId) {
        List<HuntingDropOutput> outputs = new ArrayList<>(1);
        outputs.add(new HuntingDropOutput(outputStack, chance, lootingBonus));
        return registerRecipeJei(key, targetMatcher, outputs, burningRequired, preventDuplicateStacking, entityId);
    }

    public static synchronized boolean registerRecipe(String key, HuntingTargetMatcher targetMatcher, ItemStack outputStack,
                                                      boolean preventDuplicateStacking, float chance, float lootingBonus,
                                                      ResourceLocation entityId,
                                                      Consumer<EntityLivingBase> jeiEntityConfigurator,
                                                      List<String> jeiDisplayTexts) {
        return registerRecipe(key, targetMatcher, outputStack, preventDuplicateStacking, chance, lootingBonus,
                entityId, jeiEntityConfigurator, false, jeiDisplayTexts);
    }

    public static synchronized boolean registerRecipeJei(String key, HuntingTargetMatcher targetMatcher, ItemStack outputStack,
                                                         boolean preventDuplicateStacking, float chance, float lootingBonus,
                                                         ResourceLocation entityId,
                                                         Consumer<EntityLivingBase> jeiEntityConfigurator,
                                                         List<String> jeiDisplayTexts) {
        return registerRecipeJei(key, targetMatcher, outputStack, preventDuplicateStacking, chance, lootingBonus,
                entityId, jeiEntityConfigurator, false, jeiDisplayTexts);
    }

    public static synchronized boolean registerRecipe(String key, HuntingTargetMatcher targetMatcher, ItemStack outputStack,
                                                      boolean preventDuplicateStacking, float chance, float lootingBonus,
                                                      ResourceLocation entityId,
                                                      Consumer<EntityLivingBase> jeiEntityConfigurator,
                                                      boolean runJeiEntityConfiguratorEveryTick,
                                                      List<String> jeiDisplayTexts) {
        List<HuntingDropOutput> outputs = new ArrayList<>(1);
        outputs.add(new HuntingDropOutput(outputStack, chance, lootingBonus));
        return registerRecipe(key, targetMatcher, outputs, preventDuplicateStacking, entityId,
                jeiEntityConfigurator, runJeiEntityConfiguratorEveryTick, jeiDisplayTexts);
    }

    public static synchronized boolean registerRecipeJei(String key, HuntingTargetMatcher targetMatcher, ItemStack outputStack,
                                                         boolean preventDuplicateStacking, float chance, float lootingBonus,
                                                         ResourceLocation entityId,
                                                         Consumer<EntityLivingBase> jeiEntityConfigurator,
                                                         boolean runJeiEntityConfiguratorEveryTick,
                                                         List<String> jeiDisplayTexts) {
        List<HuntingDropOutput> outputs = new ArrayList<>(1);
        outputs.add(new HuntingDropOutput(outputStack, chance, lootingBonus));
        return registerRecipeJei(key, targetMatcher, outputs, preventDuplicateStacking, entityId,
                jeiEntityConfigurator, runJeiEntityConfiguratorEveryTick, jeiDisplayTexts);
    }

    public static synchronized boolean registerRecipe(String key, HuntingTargetMatcher targetMatcher, List<HuntingDropOutput> outputs,
                                                      boolean burningRequired, boolean preventDuplicateStacking,
                                                      ResourceLocation entityId) {
        return registerRecipe(key, targetMatcher, outputs, burningRequired, preventDuplicateStacking, entityId,
                null, false, false, getDefaultDisplayTexts(burningRequired));
    }

    public static synchronized boolean registerRecipeJei(String key, HuntingTargetMatcher targetMatcher, List<HuntingDropOutput> outputs,
                                                         boolean burningRequired, boolean preventDuplicateStacking,
                                                         ResourceLocation entityId) {
        return registerRecipe(key, targetMatcher, outputs, burningRequired, preventDuplicateStacking, entityId,
                null, false, true, getDefaultDisplayTexts(burningRequired));
    }

    public static synchronized boolean registerRecipe(String key, HuntingTargetMatcher targetMatcher, List<HuntingDropOutput> outputs,
                                                      boolean preventDuplicateStacking, ResourceLocation entityId,
                                                      Consumer<EntityLivingBase> jeiEntityConfigurator,
                                                      List<String> jeiDisplayTexts) {
        return registerRecipe(key, targetMatcher, outputs, preventDuplicateStacking, entityId,
                jeiEntityConfigurator, false, jeiDisplayTexts);
    }

    public static synchronized boolean registerRecipeJei(String key, HuntingTargetMatcher targetMatcher, List<HuntingDropOutput> outputs,
                                                         boolean preventDuplicateStacking, ResourceLocation entityId,
                                                         Consumer<EntityLivingBase> jeiEntityConfigurator,
                                                         List<String> jeiDisplayTexts) {
        return registerRecipeJei(key, targetMatcher, outputs, preventDuplicateStacking, entityId,
                jeiEntityConfigurator, false, jeiDisplayTexts);
    }

    public static synchronized boolean registerRecipe(String key, HuntingTargetMatcher targetMatcher, List<HuntingDropOutput> outputs,
                                                      boolean preventDuplicateStacking, ResourceLocation entityId,
                                                      Consumer<EntityLivingBase> jeiEntityConfigurator,
                                                      boolean runJeiEntityConfiguratorEveryTick,
                                                      List<String> jeiDisplayTexts) {
        return registerRecipe(key, targetMatcher, outputs, null, preventDuplicateStacking, entityId,
                jeiEntityConfigurator, runJeiEntityConfiguratorEveryTick, false, jeiDisplayTexts);
    }

    public static synchronized boolean registerRecipeJei(String key, HuntingTargetMatcher targetMatcher, List<HuntingDropOutput> outputs,
                                                         boolean preventDuplicateStacking, ResourceLocation entityId,
                                                         Consumer<EntityLivingBase> jeiEntityConfigurator,
                                                         boolean runJeiEntityConfiguratorEveryTick,
                                                         List<String> jeiDisplayTexts) {
        return registerRecipe(key, targetMatcher, outputs, null, preventDuplicateStacking, entityId,
                jeiEntityConfigurator, runJeiEntityConfiguratorEveryTick, true, jeiDisplayTexts);
    }

    private static synchronized boolean registerRecipe(String key, HuntingTargetMatcher targetMatcher, List<HuntingDropOutput> outputs,
                                                       Boolean burningRequired, boolean preventDuplicateStacking,
                                                       ResourceLocation entityId,
                                                       Consumer<EntityLivingBase> jeiEntityConfigurator,
                                                       boolean runJeiEntityConfiguratorEveryTick,
                                                       boolean jeiOnly,
                                                       List<String> jeiDisplayTexts) {
        if (key == null || key.trim().isEmpty() || targetMatcher == null) {
            return false;
        }

        List<HuntingDropOutput> resultOutputs = copyOutputs(outputs);
        if (resultOutputs.isEmpty()) {
            return false;
        }

        RECIPES.put(key, new HuntingDropRecipe(key, targetMatcher, resultOutputs, burningRequired,
                preventDuplicateStacking, entityId, jeiEntityConfigurator, runJeiEntityConfiguratorEveryTick,
                jeiOnly, copyDisplayTexts(jeiDisplayTexts)));
        return true;
    }

    public static synchronized boolean unregisterRecipe(String key) {
        registerDefaults();
        return RECIPES.remove(key) != null;
    }

    public static void addDrops(LivingDropsEvent event, EntityLivingBase attacker, ItemStack toolStack) {
        if (!ItemKnife.isKnife(toolStack)) {
            return;
        }

        registerDefaults();
        EntityLivingBase target = event.getEntityLiving();
        int lootingLevel = EnchantmentHelper.getLootingModifier(attacker);
        List<HuntingDropRecipe> recipes;
        synchronized (HuntingDropRecipeManager.class) {
            recipes = new ArrayList<>(RECIPES.values());
        }

        for (HuntingDropRecipe recipe : recipes) {
            if (recipe.jeiOnly) {
                continue;
            }
            if (!recipe.matches(target)) {
                continue;
            }
            for (HuntingDropOutput output : recipe.getOutputs()) {
                float chance = output.getChanceWithLooting(lootingLevel);
                if (chance < 1.0F && target.world.rand.nextFloat() >= chance) {
                    continue;
                }
                addExtraDrop(event, output.getOutputStack(), recipe.preventDuplicateStacking);
            }
        }
    }

    public static List<HuntingDropRecipeView> getRecipes() {
        registerDefaults();
        List<HuntingDropRecipeView> views = new ArrayList<>();
        synchronized (HuntingDropRecipeManager.class) {
            for (HuntingDropRecipe recipe : RECIPES.values()) {
                views.add(recipe.toView());
            }
        }
        return views;
    }

    public static synchronized void registerDefaults() {
        if (defaultsRegistered) {
            return;
        }
        defaultsRegistered = true;

        registerRecipe("farmersdelight:ham", HuntingDropRecipeManager::isPigLikeAdult,
                Collections.singletonList(new HuntingDropOutput(itemStack("ham"), HAM_DROP_CHANCE, LOOTING_BONUS)),
                false, true, new ResourceLocation("minecraft", "pig"));
        registerRecipe("farmersdelight:smoked_ham", HuntingDropRecipeManager::isPigLikeAdult,
                Collections.singletonList(new HuntingDropOutput(itemStack("smoked_ham"), HAM_DROP_CHANCE, LOOTING_BONUS)),
                true, true, new ResourceLocation("minecraft", "pig"));
        registerRecipe("farmersdelight:feather", target -> target instanceof EntityChicken,
                new ItemStack(Items.FEATHER), true, 1.0F, 0.0F, false,
                new ResourceLocation("minecraft", "chicken"));
        registerRecipe("farmersdelight:leather", HuntingDropRecipeManager::isLeatherSource,
                new ItemStack(Items.LEATHER), true, 1.0F, 0.0F, false,
                new ResourceLocation("minecraft", "cow"));
        registerRecipe("farmersdelight:rabbit_hide", target -> target instanceof EntityRabbit,
                new ItemStack(Items.RABBIT_HIDE), true, 1.0F, 0.0F, false,
                new ResourceLocation("minecraft", "rabbit"));
        registerRecipe("farmersdelight:shulker_shell", target -> target instanceof EntityShulker,
                new ItemStack(Items.SHULKER_SHELL), true, 1.0F, 0.0F, false,
                new ResourceLocation("minecraft", "shulker"));
        registerRecipe("farmersdelight:string", target -> target instanceof EntitySpider || target instanceof EntityCaveSpider,
                new ItemStack(Items.STRING), true, 1.0F, 0.0F, false,
                new ResourceLocation("minecraft", "spider"));
    }

    private static List<HuntingDropOutput> copyOutputs(List<HuntingDropOutput> outputs) {
        if (outputs == null || outputs.isEmpty()) {
            return Collections.emptyList();
        }
        List<HuntingDropOutput> copiedOutputs = new ArrayList<>();
        for (HuntingDropOutput output : outputs) {
            if (output == null || output.isEmpty()) {
                continue;
            }
            copiedOutputs.add(new HuntingDropOutput(output.getOutputStack(), output.getChance(), output.getLootingBonus()));
        }
        return copiedOutputs.isEmpty() ? Collections.emptyList() : ImmutableList.copyOf(copiedOutputs);
    }

    private static ItemStack itemStack(String itemPath) {
        Item item = ModItems.get(itemPath);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static List<String> getDefaultDisplayTexts(boolean burningRequired) {
        if (!burningRequired) {
            return Collections.emptyList();
        }
        return Collections.singletonList("farmersdelight.jei.hunting_drops.burning");
    }

    private static List<String> copyDisplayTexts(List<String> displayTexts) {
        if (displayTexts == null || displayTexts.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> copiedTexts = new ArrayList<>();
        for (String displayText : displayTexts) {
            if (displayText != null && !displayText.trim().isEmpty()) {
                copiedTexts.add(displayText);
            }
        }
        return copiedTexts.isEmpty() ? Collections.emptyList() : ImmutableList.copyOf(copiedTexts);
    }

    private static void addExtraDrop(LivingDropsEvent event, ItemStack stack, boolean preventDuplicateStacking) {
        if (stack.isEmpty()) {
            return;
        }

        if (preventDuplicateStacking) {
            for (EntityItem entityItem : event.getDrops()) {
                ItemStack dropStack = entityItem.getItem();
                if (!dropStack.isEmpty() && areStacksMergeable(dropStack, stack)) {
                    dropStack.grow(stack.getCount());
                    return;
                }
            }
        }

        EntityLivingBase target = event.getEntityLiving();
        event.getDrops().add(new EntityItem(target.world, target.posX, target.posY, target.posZ, stack.copy()));
    }

    private static boolean areStacksMergeable(ItemStack firstStack, ItemStack secondStack) {
        return firstStack.getItem() == secondStack.getItem()
                && firstStack.getMetadata() == secondStack.getMetadata()
                && ItemStack.areItemStackTagsEqual(firstStack, secondStack);
    }

    private static boolean isPigLikeAdult(EntityLivingBase target) {
        return (isPigLike(target) || isHoglinLike(target)) && !isJuvenile(target);
    }

    private static boolean isPigLike(EntityLivingBase target) {
        return target instanceof EntityPig;
    }

    private static boolean isHoglinLike(EntityLivingBase target) {
        ResourceLocation entityId = EntityList.getKey(target);
        return entityId != null && "hoglin".equals(entityId.getPath());
    }

    private static boolean isJuvenile(EntityLivingBase target) {
        try {
            Object result = target.getClass().getMethod("isChild").invoke(target);
            return result instanceof Boolean && (Boolean) result;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private static boolean isLeatherSource(EntityLivingBase target) {
        if (target instanceof EntityCow || target instanceof EntityMooshroom) {
            return true;
        }
        if (target instanceof AbstractHorse) {
            return true;
        }
        ResourceLocation entityId = EntityList.getKey(target);
        return entityId != null && "trader_llama".equals(entityId.getPath());
    }

    @FunctionalInterface
    public interface HuntingTargetMatcher {
        boolean matches(EntityLivingBase target);
    }

    private static final class HuntingDropRecipe {
        private final String key;
        private final HuntingTargetMatcher targetMatcher;
        private final List<HuntingDropOutput> outputs;
        private final Boolean burningRequired;
        private final boolean preventDuplicateStacking;
        private final ResourceLocation entityId;
        private final Consumer<EntityLivingBase> jeiEntityConfigurator;
        private final boolean runJeiEntityConfiguratorEveryTick;
        private final boolean jeiOnly;
        private final List<String> jeiDisplayTexts;

        private HuntingDropRecipe(String key, HuntingTargetMatcher targetMatcher, List<HuntingDropOutput> outputs,
                                  Boolean burningRequired, boolean preventDuplicateStacking, ResourceLocation entityId,
                                  Consumer<EntityLivingBase> jeiEntityConfigurator,
                                  boolean runJeiEntityConfiguratorEveryTick,
                                  boolean jeiOnly,
                                  List<String> jeiDisplayTexts) {
            this.key = key;
            this.targetMatcher = targetMatcher;
            this.outputs = outputs;
            this.burningRequired = burningRequired;
            this.preventDuplicateStacking = preventDuplicateStacking;
            this.entityId = entityId;
            this.jeiEntityConfigurator = jeiEntityConfigurator;
            this.runJeiEntityConfiguratorEveryTick = runJeiEntityConfiguratorEveryTick;
            this.jeiOnly = jeiOnly;
            this.jeiDisplayTexts = copyDisplayTexts(jeiDisplayTexts);
        }

        private boolean matches(EntityLivingBase target) {
            return this.targetMatcher.matches(target)
                    && (this.burningRequired == null || target.isBurning() == this.burningRequired);
        }

        private List<HuntingDropOutput> getOutputs() {
            return copyOutputs(this.outputs);
        }

        private HuntingDropRecipeView toView() {
            return new HuntingDropRecipeView(this.key, this.entityId, getKnifeToolOptions(), this.outputs,
                    this.burningRequired != null && this.burningRequired, this.jeiEntityConfigurator,
                    this.runJeiEntityConfiguratorEveryTick, this.jeiDisplayTexts);
        }
    }

    public static final class HuntingDropRecipeView {
        private final String key;
        private final ResourceLocation entityId;
        private final List<ItemStack> toolOptions;
        private final List<HuntingDropOutput> outputs;
        private final boolean burningRequired;
        private final Consumer<EntityLivingBase> jeiEntityConfigurator;
        private final boolean runJeiEntityConfiguratorEveryTick;
        private final List<String> jeiDisplayTexts;

        private HuntingDropRecipeView(String key, ResourceLocation entityId, List<ItemStack> toolOptions,
                                      List<HuntingDropOutput> outputs, boolean burningRequired,
                                      Consumer<EntityLivingBase> jeiEntityConfigurator,
                                      boolean runJeiEntityConfiguratorEveryTick,
                                      List<String> jeiDisplayTexts) {
            this.key = key;
            this.entityId = entityId;
            this.toolOptions = toolOptions;
            this.outputs = copyOutputs(outputs);
            this.burningRequired = burningRequired;
            this.jeiEntityConfigurator = jeiEntityConfigurator;
            this.runJeiEntityConfiguratorEveryTick = runJeiEntityConfiguratorEveryTick;
            this.jeiDisplayTexts = copyDisplayTexts(jeiDisplayTexts);
        }

        public String getKey() {
            return this.key;
        }

        public ResourceLocation getEntityId() {
            return this.entityId;
        }

        public List<ItemStack> getToolOptions() {
            return this.toolOptions;
        }

        public ItemStack getOutputStack() {
            return this.outputs.isEmpty() ? ItemStack.EMPTY : this.outputs.get(0).getOutputStack();
        }

        public List<ItemStack> getOutputStacks() {
            List<ItemStack> stacks = new ArrayList<>();
            for (HuntingDropOutput output : this.outputs) {
                stacks.add(output.getOutputStack());
            }
            return stacks.isEmpty() ? Collections.emptyList() : ImmutableList.copyOf(stacks);
        }

        public List<HuntingDropOutput> getOutputs() {
            return copyOutputs(this.outputs);
        }

        public float getOutputChance(int outputIndex) {
            if (outputIndex < 0 || outputIndex >= this.outputs.size()) {
                return 1.0F;
            }
            return this.outputs.get(outputIndex).getChance();
        }

        public float getOutputLootingBonus(int outputIndex) {
            if (outputIndex < 0 || outputIndex >= this.outputs.size()) {
                return 0.0F;
            }
            return this.outputs.get(outputIndex).getLootingBonus();
        }

        public boolean isBurningVariant() {
            return this.burningRequired;
        }

        public Consumer<EntityLivingBase> getJeiEntityConfigurator() {
            return this.jeiEntityConfigurator;
        }

        public boolean shouldRunJeiEntityConfiguratorEveryTick() {
            return this.runJeiEntityConfiguratorEveryTick;
        }

        public List<String> getJeiDisplayTexts() {
            return this.jeiDisplayTexts;
        }
    }

    private static List<ItemStack> getKnifeToolOptions() {
        return KnifeItemStacks.getJeiDisplayStacks();
    }
}
