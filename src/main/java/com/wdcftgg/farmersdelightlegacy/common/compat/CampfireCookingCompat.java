package com.wdcftgg.farmersdelightlegacy.common.compat;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.common.recipe.CampfireCookingRecipe;
import com.wdcftgg.farmersdelightlegacy.common.recipe.manager.CampfireCookingRecipeManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CampfireCookingCompat {

    private static final String CAMPFIRE_MOD_ID = "campfire";
    private static final String FUTURE_MC_MOD_ID = "futuremc";
    private static final float DEFAULT_EXPERIENCE = 0.35F;

    private CampfireCookingCompat() {
    }

    public static void registerAll() {
        List<CampfireCookingRecipe> recipes = CampfireCookingRecipeManager.getRecipes();
        registerCampfireRecipes(recipes);
        registerFutureMcRecipes(recipes);
    }

    private static void registerCampfireRecipes(List<CampfireCookingRecipe> recipes) {
        if (!Loader.isModLoaded(CAMPFIRE_MOD_ID)) {
            return;
        }

        try {
            Class<?> handlerClass = Class.forName("git.jbredwards.campfire.common.recipe.campfire.CampfireRecipeHandler");
            Method createRecipeMethod = handlerClass.getMethod("createRecipe", List.class, ItemStack.class, int.class, float.class);
            int registeredCount = 0;
            for (CampfireCookingRecipe recipe : recipes) {
                List<ItemStack> inputs = expandInputs(recipe);
                if (inputs.isEmpty() || recipe.getResultStack().isEmpty()) {
                    continue;
                }
                createRecipeMethod.invoke(null, inputs, recipe.getResultStack(), recipe.getCookingTime(), DEFAULT_EXPERIENCE);
                registeredCount++;
            }
            FarmersDelightLegacy.LOGGER.info("Registered {} Farmers Delight campfire recipes for Campfire mod.", registeredCount);
        } catch (ReflectiveOperationException exception) {
            FarmersDelightLegacy.LOGGER.error("Failed to register Campfire mod cooking recipes.", exception);
        }
    }

    private static void registerFutureMcRecipes(List<CampfireCookingRecipe> recipes) {
        if (!Loader.isModLoaded(FUTURE_MC_MOD_ID)) {
            return;
        }

        try {
            Class<?> recipesClass = Class.forName("thedarkcolour.futuremc.recipe.campfire.CampfireRecipes");
            Object recipesInstance = recipesClass.getField("INSTANCE").get(null);
            Class<?> recipeClass = Class.forName("thedarkcolour.futuremc.recipe.campfire.CampfireRecipe");
            Constructor<?> recipeConstructor = recipeClass.getConstructor(Ingredient.class, ItemStack.class, int.class);
            Method getRecipesMethod = recipesClass.getMethod("getRecipes");
            @SuppressWarnings("unchecked")
            List<Object> futureMcRecipes = (List<Object>) getRecipesMethod.invoke(recipesInstance);
            int registeredCount = 0;

            for (CampfireCookingRecipe recipe : recipes) {
                List<ItemStack> inputs = expandInputs(recipe);
                ItemStack output = recipe.getResultStack();
                if (inputs.isEmpty() || output.isEmpty()) {
                    continue;
                }
                Ingredient ingredient = Ingredient.fromStacks(inputs.toArray(new ItemStack[0]));
                Object futureMcRecipe = recipeConstructor.newInstance(ingredient, output, recipe.getCookingTime());
                futureMcRecipes.add(futureMcRecipe);
                registeredCount++;
            }
            FarmersDelightLegacy.LOGGER.info("Registered {} Farmers Delight campfire recipes for Future MC campfire.", registeredCount);
        } catch (ReflectiveOperationException exception) {
            FarmersDelightLegacy.LOGGER.error("Failed to register Future MC campfire cooking recipes.", exception);
        }
    }

    private static List<ItemStack> expandInputs(CampfireCookingRecipe recipe) {
        List<ItemStack> inputs = new ArrayList<>();
        Set<String> seenKeys = new HashSet<>();
        for (CampfireCookingRecipe.IngredientEntry entry : recipe.getIngredients()) {
            if (entry.getItem() != null) {
                ItemStack stack = new ItemStack(entry.getItem(), 1, entry.getMetadata() == OreDictionary.WILDCARD_VALUE ? OreDictionary.WILDCARD_VALUE : entry.getMetadata());
                addUnique(inputs, seenKeys, stack);
                continue;
            }
            if (entry.getOreDictName() != null) {
                for (ItemStack oreStack : OreDictionary.getOres(entry.getOreDictName(), false)) {
                    ItemStack stack = oreStack.copy();
                    stack.setCount(1);
                    addUnique(inputs, seenKeys, stack);
                }
            }
        }
        return inputs;
    }

    private static void addUnique(List<ItemStack> inputs, Set<String> seenKeys, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        String key = stack.getItem().getRegistryName() + "@" + stack.getMetadata();
        if (seenKeys.add(key)) {
            inputs.add(stack);
        }
    }
}