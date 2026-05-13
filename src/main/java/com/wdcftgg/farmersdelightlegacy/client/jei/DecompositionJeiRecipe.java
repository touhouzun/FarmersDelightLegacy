package com.wdcftgg.farmersdelightlegacy.client.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class DecompositionJeiRecipe implements IRecipeWrapper {

    private final ItemStack input;
    private final ItemStack output;
    private final List<ItemStack> accelerators;

    public DecompositionJeiRecipe(ItemStack input, ItemStack output, List<ItemStack> accelerators) {
        this.input = input;
        this.output = output;
        this.accelerators = accelerators;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<List<ItemStack>> inputs = new ArrayList<>();
        List<ItemStack> organicCompostInput = new ArrayList<>();
        organicCompostInput.add(this.input);
        inputs.add(organicCompostInput);
        inputs.add(this.accelerators);
        ingredients.setInputLists(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, this.output);
    }
}