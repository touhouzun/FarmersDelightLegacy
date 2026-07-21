package com.wdcftgg.farmersdelightlegacy.client.jei;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.api.knife.IKnifeItem;
import com.wdcftgg.farmersdelightlegacy.client.gui.GuiCookingPot;
import com.wdcftgg.farmersdelightlegacy.common.item.ItemCookingPot;
import com.wdcftgg.farmersdelightlegacy.common.recipe.*;
import com.wdcftgg.farmersdelightlegacy.common.recipe.manager.*;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModItems;
import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityCookingPot;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

@JEIPlugin
public final class FarmersDelightJeiPlugin implements IModPlugin {

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        IDrawable cookingPotIcon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.COOKING_POT));
        IDrawable cuttingBoardIcon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.CUTTING_BOARD));
        IDrawable campfireIcon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.STOVE));
        DecompositionRecipeCategory decompositionCategory = new DecompositionRecipeCategory(guiHelper);
        HuntingDropRecipeCategory huntingDropCategory = new HuntingDropRecipeCategory(guiHelper);
        HarvestDropRecipeCategory harvestDropCategory = new HarvestDropRecipeCategory(guiHelper);

        registry.addRecipeCategories(
                new CookingPotRecipeCategory(guiHelper, cookingPotIcon),
                new CuttingBoardRecipeCategory(guiHelper, cuttingBoardIcon),
                new CampfireRecipeCategory(guiHelper, campfireIcon),
                decompositionCategory,
                huntingDropCategory,
                harvestDropCategory
        );
    }

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipes(buildCookingPotRecipes(), JeiUids.COOKING_POT);
        registry.addRecipes(buildCuttingBoardRecipes(), JeiUids.CUTTING_BOARD);
        registry.addRecipes(buildCampfireRecipes(), JeiUids.CAMPFIRE);
        registry.addRecipes(buildDecompositionRecipes(), JeiUids.DECOMPOSITION);
        registry.addRecipes(buildHuntingDropRecipes(), JeiUids.HUNTING_DROPS);
        registry.addRecipes(buildHarvestDropRecipes(), JeiUids.harvestDrops);
        registry.handleRecipes(SpecialCraftingJeiRecipe.class, recipe -> recipe, VanillaRecipeCategoryUid.CRAFTING);
        registry.addRecipes(buildSpecialCraftingRecipes(), VanillaRecipeCategoryUid.CRAFTING);

        registry.addRecipeCatalyst(new ItemStack(ModBlocks.COOKING_POT), JeiUids.COOKING_POT);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.CUTTING_BOARD), JeiUids.CUTTING_BOARD);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.STOVE), JeiUids.CAMPFIRE);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.SKILLET), JeiUids.CAMPFIRE);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.ORGANIC_COMPOST), JeiUids.DECOMPOSITION);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.RICH_SOIL), JeiUids.DECOMPOSITION);
        addKnifeRecipeCatalysts(registry);

        registry.addRecipeClickArea(GuiCookingPot.class, 89, 25, 24, 17, JeiUids.COOKING_POT);
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(new CookingPotRecipeTransferInfo());

        addIngredientInfoItem(registry, "wheat_dough", "farmersdelight.jei.info.dough");
        addIngredientInfoItem(registry, "straw", "farmersdelight.jei.info.straw");
        addIngredientInfoItem(registry, "ham", "farmersdelight.jei.info.ham");
        addIngredientInfoItem(registry, "smoked_ham", "farmersdelight.jei.info.ham");
        addKnifeIngredientInfos(registry);
        registerCropIngredientInfos(registry);
    }

    private static void addIngredientInfo(IModRegistry registry, ItemStack stack, String key) {
        if (!stack.isEmpty()) {
            registry.addIngredientInfo(stack, VanillaTypes.ITEM, key);
        }
    }

    private static void addIngredientInfoGroup(IModRegistry registry, String key, ItemStack... stacks) {
        List<ItemStack> validStacks = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                validStacks.add(stack);
            }
        }

        if (!validStacks.isEmpty()) {
            registry.addIngredientInfo(validStacks, VanillaTypes.ITEM, key);
        }
    }

    private static void registerCropIngredientInfos(IModRegistry registry) {
        addIngredientInfoGroup(registry, "farmersdelight.jei.info.wild_beetroots",
                new ItemStack(Items.BEETROOT),
                new ItemStack(ModBlocks.WILD_BEETROOTS));
        addIngredientInfoGroup(registry, "farmersdelight.jei.info.wild_cabbages",
                stackFromItemName("cabbage"),
                stackFromItemName("cabbage_leaf"),
                new ItemStack(ModBlocks.WILD_CABBAGES));
        addIngredientInfoGroup(registry, "farmersdelight.jei.info.wild_carrots",
                new ItemStack(Items.CARROT),
                new ItemStack(ModBlocks.WILD_CARROTS));
        addIngredientInfoGroup(registry, "farmersdelight.jei.info.wild_onions",
                stackFromItemName("onion"),
                new ItemStack(ModBlocks.WILD_ONIONS));
        addIngredientInfoGroup(registry, "farmersdelight.jei.info.wild_potatoes",
                new ItemStack(Items.POTATO),
                new ItemStack(ModBlocks.WILD_POTATOES));
        addIngredientInfoGroup(registry, "farmersdelight.jei.info.wild_rice",
                stackFromItemName("rice"),
                stackFromItemName("rice_panicle"),
                new ItemStack(ModBlocks.WILD_RICE));
        addIngredientInfoGroup(registry, "farmersdelight.jei.info.wild_tomatoes",
                stackFromItemName("tomato"),
                new ItemStack(ModBlocks.WILD_TOMATOES));
    }

    private static ItemStack stackFromItemName(String itemName) {
        Item item = ModItems.ITEMS.get(itemName);
        return item != null ? new ItemStack(item) : ItemStack.EMPTY;
    }

    private static void addIngredientInfoItem(IModRegistry registry, String itemName, String key) {
        addIngredientInfo(registry, stackFromItemName(itemName), key);
    }

    private static List<CookingPotJeiRecipe> buildCookingPotRecipes() {
        List<CookingPotJeiRecipe> result = new ArrayList<>();
        for (CookingPotRecipe recipe : CookingPotRecipeManager.getRecipes()) {
            result.add(CookingPotJeiRecipe.of(recipe));
        }
        return result;
    }

    private static List<CuttingBoardJeiRecipe> buildCuttingBoardRecipes() {
        List<CuttingBoardJeiRecipe> result = new ArrayList<>();
        for (CuttingBoardRecipeManager.CuttingBoardRecipeView recipe : CuttingBoardRecipeManager.getRecipes()) {
            result.add(CuttingBoardJeiRecipe.of(recipe));
        }
        return result;
    }

    private static List<CampfireJeiRecipe> buildCampfireRecipes() {
        List<CampfireJeiRecipe> result = new ArrayList<>();
        for (CampfireCookingRecipe recipe : CampfireCookingRecipeManager.getRecipes()) {
            result.add(CampfireJeiRecipe.of(recipe));
        }
        return result;
    }


    private static List<HuntingDropJeiRecipe> buildHuntingDropRecipes() {
        List<HuntingDropJeiRecipe> result = new ArrayList<>();
        for (HuntingDropRecipeManager.HuntingDropRecipeView recipe : HuntingDropRecipeManager.getRecipes()) {
            result.add(HuntingDropJeiRecipe.of(recipe));
        }
        return result;
    }

    private static List<HarvestDropJeiRecipe> buildHarvestDropRecipes() {
        List<HarvestDropJeiRecipe> result = new ArrayList<>();
        for (HarvestDropRecipeManager.HarvestDropRecipeView recipe : HarvestDropRecipeManager.getRecipes()) {
            result.add(HarvestDropJeiRecipe.of(recipe));
        }
        return result;
    }

    private static void addKnifeRecipeCatalysts(IModRegistry registry) {
        for (ItemStack stack : KnifeItemManager.getJeiDisplayStacks()) {
            registry.addRecipeCatalyst(stack, JeiUids.HUNTING_DROPS);
            registry.addRecipeCatalyst(stack, JeiUids.harvestDrops);
        }
    }

    private static void addKnifeIngredientInfos(IModRegistry registry) {
        IStackHelper stackHelper = registry.getJeiHelpers().getStackHelper();
        Set<String> registeredInfoStacks = new LinkedHashSet<>();
        for (Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
            if (!(item instanceof IKnifeItem)) {
                continue;
            }
            List<ItemStack> stacks = stackHelper.getSubtypes(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
            if (stacks.isEmpty()) {
                stacks = Collections.singletonList(new ItemStack(item));
            }
            for (ItemStack stack : stacks) {
                if (!KnifeItemManager.isKnife(stack)) {
                    continue;
                }
                ItemStack infoStack = ((IKnifeItem) item).getKnifeJeiInfoStack(stack);
                String infoStackKey = getItemStackInfoKey(infoStack);
                if (!infoStack.isEmpty() && registeredInfoStacks.add(infoStackKey)) {
                    registry.addIngredientInfo(infoStack, VanillaTypes.ITEM, "farmersdelight.jei.info.knife");
                }
            }
        }
    }

    private static String getItemStackInfoKey(ItemStack stack) {
        if (stack.isEmpty()) {
            return "empty";
        }
        return Item.REGISTRY.getNameForObject(stack.getItem()) + ":" + stack.getMetadata() + ":" + stack.getTagCompound();
    }

    private static List<DecompositionJeiRecipe> buildDecompositionRecipes() {
        List<ItemStack> accelerators = DecompositionRecipeManager.getAcceleratorDisplayStacks();
        List<DecompositionJeiRecipe> recipes = new ArrayList<>();
        recipes.add(new DecompositionJeiRecipe(new ItemStack(ModBlocks.ORGANIC_COMPOST), new ItemStack(ModBlocks.RICH_SOIL), accelerators));
        return recipes;
    }
    private static List<SpecialCraftingJeiRecipe> buildSpecialCraftingRecipes() {
        List<SpecialCraftingJeiRecipe> recipes = new ArrayList<>();
        addWaterDoughRecipe(recipes);
        return recipes;
    }

    private static void addWaterDoughRecipe(List<SpecialCraftingJeiRecipe> recipes) {
        ResourceLocation recipeId = new ResourceLocation(FarmersDelightLegacy.MOD_ID, "wheat_dough_from_water");
        if (!ForgeRegistries.RECIPES.containsKey(recipeId)) {
            return;
        }

        ItemStack wheatDough = stackFromItemName("wheat_dough");
        if (wheatDough.isEmpty()) {
            return;
        }

        List<List<ItemStack>> inputs = new ArrayList<>();
        inputs.add(singleStackList(new ItemStack(Items.WHEAT)));
        inputs.add(singleStackList(new ItemStack(Items.WATER_BUCKET)));
        recipes.add(new SpecialCraftingJeiRecipe(inputs, wheatDough));
    }

    private static void addFoodServingRecipes(List<SpecialCraftingJeiRecipe> recipes) {
        Map<String, SpecialCraftingJeiRecipe> uniqueRecipes = new LinkedHashMap<>();

        for (CookingPotRecipe cookingPotRecipe : CookingPotRecipeManager.getRecipes()) {
            ItemStack mealStack = cookingPotRecipe.getResultStack().copy();
            if (mealStack.isEmpty()) {
                continue;
            }

            ItemStack potStack = new ItemStack(ModBlocks.COOKING_POT);
            ItemStack configuredContainer = cookingPotRecipe.getOutputContainer();
            boolean useDefaultContainer = !cookingPotRecipe.hasContainerDefinition();
            TileEntityCookingPot.writeMealToItem(potStack, mealStack, configuredContainer, useDefaultContainer);

            ItemStack servingContainer = ItemCookingPot.inferContainer(potStack, mealStack);
            if (servingContainer.isEmpty()) {
                continue;
            }

            ItemStack output = mealStack.copy();
            output.setCount(1);

            List<List<ItemStack>> inputs = new ArrayList<>();
            inputs.add(singleStackList(potStack));
            inputs.add(singleStackList(servingContainer));

            String key = output.getItem().getRegistryName() + "|" + output.getMetadata() + "|"
                    + servingContainer.getItem().getRegistryName() + "|" + servingContainer.getMetadata();
            uniqueRecipes.putIfAbsent(key, new SpecialCraftingJeiRecipe(inputs, output));
        }

        recipes.addAll(uniqueRecipes.values());
    }

    private static List<ItemStack> singleStackList(ItemStack stack) {
        List<ItemStack> result = new ArrayList<>(1);
        result.add(stack);
        return result;
    }
}
