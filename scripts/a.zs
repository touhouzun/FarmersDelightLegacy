import crafttweaker.item.IIngredient;
import crafttweaker.item.IItemStack;
import crafttweaker.world.IBlockPos;
import crafttweaker.world.IWorld;
import crafttweaker.block.IBlockState;

mods.farmersdelight.HeatSource.addDirectHeatSourcePredicate(
    "mypack:dynamic_direct",
    function(world as IWorld, pos as IBlockPos, state as IBlockState) as bool {
        return state.block.definition.id == "minecraft:ice";
    }
);

mods.farmersdelight.HeatSource.addOffsetPredicate(
    "mypack:dynamic_offset",
    function(world as IWorld, pos as IBlockPos, state as IBlockState) as bool {
        return state.block.definition.id == "minecraft:bedrock";
    }
);

mods.farmersdelight.Campfire.addRecipe("k1", [<ore:listAllbeefraw> as IIngredient], <minecraft:shears:2> as IItemStack);

mods.farmersdelight.CookingPot.addRecipe(
    "k2",
    [<minecraft:carrot> as IIngredient, <ore:listAllbeefraw> as IIngredient],
    <minecraft:book> as IItemStack
);

mods.farmersdelight.CuttingBoard.addRecipe(
    "k3",
    [<minecraft:pumpkin>] as IIngredient[],
    [<minecraft:apple> * 4] as IItemStack[]
);