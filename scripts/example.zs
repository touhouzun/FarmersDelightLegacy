import crafttweaker.block.IBlockState;
import crafttweaker.entity.IEntity;
import crafttweaker.item.IIngredient;
import crafttweaker.item.IItemStack;
import crafttweaker.world.IBlockPos;
import crafttweaker.world.IWorld;

// Farmers Delight Legacy 的 CraftTweaker 示例脚本。
// CraftTweaker example script for Farmers Delight Legacy.
//
// 说明 / Notes:
// 1. 本文件演示当前已实现的 CRT 接口。
//    This file demonstrates the currently implemented CRT APIs.
// 2. 配方 key 建议始终使用你自己的命名空间，避免与别的脚本冲突。
//    Always use your own namespace for recipe keys to avoid conflicts with other scripts.
// 3. 厨锅支持“默认容器 / 指定容器 / 显式无容器”。
//    Cooking Pot supports "default container / explicit container / explicitly no container".
// 4. 砧板支持“默认刀 / 指定工具 / 显式无工具”。
//    Cutting Board supports "default knife / explicit tool / explicitly no tool".
// 5. 现在也支持“按输出物品批量删除配方”。
//    Removing recipes by output item is also supported now.
// 6. 狩猎掉落使用实体注册 ID；收获掉落使用方块注册 ID 和 metadata。
//    Hunting Drops use entity registry IDs; Harvest Drops use block registry IDs and metadata.


// =========================
// 热源接口示例 / Heat Source Examples
// =========================

// 将点燃的熔炉注册为直接热源。
// Register a lit furnace as a direct heat source.
mods.farmersdelight.HeatSource.addDirectHeatSourceBlock(
    "example:lit_furnace_direct_heat",
    "minecraft:lit_furnace"
);

// 将漏斗注册为“热源检测下移一格”的方块。
// Register a hopper as a block that offsets heat detection downward by one block.
mods.farmersdelight.HeatSource.addOffsetBlock(
    "example:hopper_heat_offset",
    "minecraft:hopper"
);

// 将岩浆块从默认直接热源中移除。
// Remove magma blocks from the default direct heat sources.
mods.farmersdelight.HeatSource.removeDefaultDirectHeatSourceBlock(
    "example:remove_magma_direct_heat",
    "minecraft:magma"
);

// 将指定 metadata 的方块从默认直接热源中移除。
// Remove a block with exact metadata from the default direct heat sources.
mods.farmersdelight.HeatSource.removeDefaultDirectHeatSourceBlockWithMeta(
    "example:remove_lit_furnace_meta_0_direct_heat",
    "minecraft:lit_furnace",
    0
);

// 通过回调动态判定要从默认直接热源中移除的方块。
// Dynamically determine default direct heat sources to remove with a callback.
mods.farmersdelight.HeatSource.removeDefaultDirectHeatSourcePredicate(
    "example:remove_lava_direct_heat",
    function(world as IWorld, pos as IBlockPos, state as IBlockState) as bool {
        return state.block.definition.id == "minecraft:lava" || state.block.definition.id == "minecraft:flowing_lava";
    }
);

// 恢复指定 key 移除的默认直接热源。
// Restore default direct heat sources removed by the given key.
// mods.farmersdelight.HeatSource.restoreDefaultDirectHeatSourceBlock("example:remove_magma_direct_heat");

// 通过回调动态判定直接热源。
// Dynamically determine direct heat sources with a callback.
mods.farmersdelight.HeatSource.addDirectHeatSourcePredicate(
    "example:ice_direct_heat",
    function(world as IWorld, pos as IBlockPos, state as IBlockState) as bool {
        return state.block.definition.id == "minecraft:ice";
    }
);

// 通过回调动态判定是否需要把检测位置下移一格。
// Dynamically determine whether the heat check should be offset downward.
mods.farmersdelight.HeatSource.addOffsetPredicate(
    "example:bedrock_offset_heat",
    function(world as IWorld, pos as IBlockPos, state as IBlockState) as bool {
        return state.block.definition.id == "minecraft:bedrock";
    }
);


// =========================
// 营火配方示例 / Campfire Recipe Examples
// =========================

// 基础写法：使用 IIngredient / IItemStack。
// Basic form: uses IIngredient / IItemStack.
mods.farmersdelight.Campfire.addRecipe(
    "example:campfire_beef",
    [<ore:listAllbeefraw> as IIngredient],
    <minecraft:cooked_beef> as IItemStack
);

// 高级写法：字符串版，可自定义产物数量和烹饪时间。
// Advanced form: string version with custom output count and cooking time.
mods.farmersdelight.Campfire.addRecipeAdvanced(
    "example:campfire_baked_potato_fast",
    ["minecraft:potato"],
    "minecraft:baked_potato",
    1,
    200
);


// =========================
// 厨锅配方示例 / Cooking Pot Recipe Examples
// =========================

// 默认容器逻辑：
// 如果产物自己有容器返回物，则优先使用该容器；
// 如果没有，则按模组默认逻辑处理，多数餐食会默认使用碗。
// Default container logic:
// If the result item already defines a crafting remainder/container, that container is used first;
// otherwise the mod falls back to its default logic, and most meals will use bowls by default.
mods.farmersdelight.CookingPot.addRecipe(
    "example:pot_default_container",
    [<minecraft:carrot>, <minecraft:potato>] as IIngredient[],
    <minecraft:rabbit_stew>
);

// 显式指定容器：
// 这个配方最终只能用碗来盛装。
// Explicit container:
// This recipe can only be served with bowls.
mods.farmersdelight.CookingPot.addRecipeWithContainer(
    "example:pot_force_bowl",
    [<minecraft:apple>, <minecraft:sugar>] as IIngredient[],
    <minecraft:cookie>,
    <minecraft:bowl>
);

// 显式无容器：
// 即使产物没有默认容器，这里也明确声明“不需要额外容器”。
// Explicitly no container:
// Even if the result has no default container, this explicitly marks the recipe as requiring none.
mods.farmersdelight.CookingPot.addRecipeWithoutContainer(
    "example:pot_without_container",
    [<minecraft:wheat>, <minecraft:sugar>] as IIngredient[],
    <minecraft:bread>
);

// 输入返还物：支持 CraftTweaker 的 transformReplace。
// Input remainder: CraftTweaker transformReplace is supported.
mods.farmersdelight.CookingPot.addRecipeWithContainer(
    "example:pot_transform_replace",
    [<minecraft:milk_bucket>.transformReplace(<minecraft:bucket>)] as IIngredient[],
    <minecraft:cookie>,
    <minecraft:bowl>
);

// 字符串版模板。
// String overload templates.
// 字符串物品 token 使用 modid:item@meta 表示精确 metadata。
// String item tokens use modid:item@meta for exact metadata matching.
// 例如 / Example:
// "minecraft:fish@1" 代表 metadata 为 1 的 minecraft:fish。
// "minecraft:fish@1" represents minecraft:fish with metadata 1.
// mods.farmersdelight.CookingPot.addRecipeWithContainer(
//     "yourpack:pot_string_container",
//     ["minecraft:fish@1", "ore:listAllbeefraw"],
//     "minecraft:rabbit_stew",
//     1,
//     "minecraft:bowl",
//     1
// );
//
// mods.farmersdelight.CookingPot.addRecipeWithoutContainer(
//     "yourpack:pot_string_no_container",
//     ["minecraft:wheat", "minecraft:sugar"],
//     "minecraft:bread",
//     1
// );
//
// mods.farmersdelight.CookingPot.addRecipeAdvanced(
//     "yourpack:pot_full_control",
//     ["minecraft:beetroot", "minecraft:beetroot", "minecraft:beetroot"],
//     "minecraft:beetroot_soup",
//     1,
//     "minecraft:bowl",
//     1,
//     120,
//     1.0,
//     true
// );


// =========================
// 砧板配方示例 / Cutting Board Recipe Examples
// =========================

// 默认写法：默认要求工具为刀。
// Default form: requires a knife by default.
mods.farmersdelight.CuttingBoard.addRecipe(
    "example:board_default_knife",
    [<minecraft:pumpkin>] as IIngredient[],
    [<minecraft:pumpkin_seeds> * 4] as IItemStack[]
);

// 指定工具：这个配方只能用斧头处理。
// Explicit tool: this recipe can only be processed with an axe.
mods.farmersdelight.CuttingBoard.addRecipeWithTool(
    "example:board_axe_only",
    [<minecraft:log>] as IIngredient[],
    [<ore:toolAxe>] as IIngredient[],
    [<minecraft:planks> * 4, <farmersdelight:tree_bark>] as IItemStack[]
);

// 显式无工具：把物品放到砧板后，空手主手右击即可处理。
// Explicitly no tool: place the item on the board and right-click with an empty main hand to process it.
mods.farmersdelight.CuttingBoard.addRecipeWithoutTool(
    "example:board_hand_only",
    [<minecraft:melon_block>] as IIngredient[],
    [<minecraft:melon> * 4] as IItemStack[]
);

// 带掉率的完整写法模板。
// Full template with result chances.
// mods.farmersdelight.CuttingBoard.addRecipeAdvanced(
//     "yourpack:board_full_control",
//     [<minecraft:fish:1>] as IIngredient[],
//     [<ore:toolKnife>] as IIngredient[],
//     [<minecraft:fish:1> * 2, <minecraft:dye:15>] as IItemStack[],
//     [1.0, 0.25] as float[]
// );
//
// 字符串版模板。
// String overload templates.
// 字符串物品 token 使用 modid:item@meta 表示精确 metadata。
// String item tokens use modid:item@meta for exact metadata matching.
// mods.farmersdelight.CuttingBoard.addRecipeWithTool(
//     "yourpack:board_string_tool",
//     ["minecraft:fish@1"],
//     ["ore:toolAxe"],
//     ["minecraft:fish@1", "farmersdelight:tree_bark"]
// );
//
// mods.farmersdelight.CuttingBoard.addRecipeWithoutTool(
//     "yourpack:board_string_no_tool",
//     ["minecraft:melon_block"],
//     ["minecraft:melon"]
// );


// =========================
// 小刀管理示例 / Knife Management Examples
// =========================

// 新增小刀：同时影响狩猎掉落、收获掉落与左侧 JEI 刀具输入。
// Add a knife: affects Hunting Drops, Harvest Drops, and the left-hand JEI knife input.
// mods.farmersdelight.Knife.addKnife(<minecraft:iron_sword>);

// 精确移除一个物品和 metadata；若需匹配全部 metadata，可在物品堆叠中使用通配 metadata。
// Remove one item and metadata exactly; use wildcard metadata in the item stack to match all metadata variants.
// mods.farmersdelight.Knife.removeKnife(<minecraft:iron_sword>);

// 按 Item 的运行时类移除：会移除所有 item instanceof 该物品类的小刀堆叠。
// Remove by Item runtime class: removes every knife stack whose item is an instance of this item's class.
// mods.farmersdelight.Knife.removeKnifeItem(<farmersdelight:iron_knife>);

// 仅调整狩猎/收割 JEI 配方左侧刀具输入展示，不影响实际掉落资格。
// Change only the left-hand knife input shown in Hunting Drops / Harvest Drops JEI recipes.
// mods.farmersdelight.Knife.addJeiDisplayStack(<minecraft:iron_sword>);
// mods.farmersdelight.Knife.removeJeiDisplayStack(<minecraft:iron_sword>);
// mods.farmersdelight.Knife.removeJeiDisplayItem(<farmersdelight:iron_knife>);

// 查询当前实际生效的小刀，以及 JEI 左侧显示的小刀。
// Query gameplay-effective knives and the knives shown on the left side of JEI recipes.
// val activeKnives = mods.farmersdelight.Knife.getHuntingAndHarvestKnifeStacks();
// val jeiKnives = mods.farmersdelight.Knife.getJeiDisplayStacks();

// =========================
// 狩猎掉落示例 / Hunting Drop Examples
// =========================

// 传入 CRT IEntityDefinition；用刀击杀奶牛时额外掉落皮革。
// Pass a CraftTweaker IEntityDefinition; killing a cow with a knife drops extra leather.
mods.farmersdelight.HuntingDrop.addRecipe(
    "example:hunting_cow_leather",
    <entity:minecraft:cow>,
    <minecraft:leather>
);

// 完整写法：每个输出可分别指定基础掉率和每级抢夺加成。
// Advanced form: each output can define a base chance and a Looting bonus per level.
// mods.farmersdelight.HuntingDrop.addRecipeAdvanced(
//     "yourpack:hunting_blaze_rewards",
//     <entity:minecraft:blaze>,
//     [<minecraft:blaze_rod>, <minecraft:glowstone_dust> * 2] as IItemStack[],
//     [0.5, 1.0] as float[],
//     [0.1, 0.0] as float[],
//     true,
//     true
// );
//
// addJeiRecipe / addJeiRecipeAdvanced 只显示在 JEI，不会在游戏中实际掉落。
// addJeiRecipe / addJeiRecipeAdvanced only display in JEI and never create in-game drops.

// JEI 实体状态配置：回调只影响狩猎掉落 JEI 预览实体，不影响实际战斗或掉落。
// JEI entity configuration: the callback only changes the Hunting Drop JEI preview entity, never real combat or drops.
// mods.farmersdelight.HuntingDrop.addJeiRecipeWithEntityConfigurator(
//     "yourpack:hunting_blaze_preview",
//     <entity:minecraft:blaze>,
//     <minecraft:blaze_rod>,
//     function(entity as IEntity) {
//         entity.setFire(1000000);
//         entity.setCustomName("JEI Preview");
//     }
// );
//
// 高级配置器可传入 true 以在每个 JEI 更新周期重新应用状态。
// Advanced configurators can pass true to reapply the state during every JEI update cycle.


// =========================
// 收获掉落示例 / Harvest Drop Examples
// =========================

// 传入 CRT IBlockState；用刀收获枯死灌木时额外掉落线。
// Pass a CraftTweaker IBlockState; harvesting dead bushes with a knife drops extra string.
mods.farmersdelight.HarvestDrop.addRecipe(
    "example:harvest_dead_bush_string",
    <blockstate:minecraft:deadbush>,
    <minecraft:string>
);

// IBlockState 会完整匹配方块及其属性，可用于指定成熟度等状态。
// IBlockState matches the complete block and property state, allowing specific maturity and similar states.
// val grassState = <blockstate:minecraft:tallgrass>.withProperty("type", "grass");
// mods.farmersdelight.HarvestDrop.addRecipeAdvanced(
//     "yourpack:harvest_grass_rewards",
//     grassState,
//     [<minecraft:wheat_seeds>, <minecraft:string>] as IItemStack[],
//     [0.25, 0.1] as float[],
//     [0.05, 0.0] as float[],
//     true
// );
//
// addJeiRecipe / addJeiRecipeAdvanced 只显示在 JEI，不会在游戏中实际掉落。
// addJeiRecipe / addJeiRecipeAdvanced only display in JEI and never create in-game drops.


// =========================
// 删除配方模板 / Recipe Removal Templates
// =========================

// 按 key 删除单个配方。
// Remove a single recipe by key.
// mods.farmersdelight.Campfire.removeRecipe("example:campfire_beef");
// mods.farmersdelight.CookingPot.removeRecipe("example:pot_force_bowl");
// mods.farmersdelight.CuttingBoard.removeRecipe("example:board_hand_only");
// mods.farmersdelight.HuntingDrop.removeRecipe("example:hunting_cow_leather");
// mods.farmersdelight.HarvestDrop.removeRecipe("example:harvest_dead_bush_string");

// 按输出物品批量删除所有同产物配方。
// Remove all recipes that share the same output item.
// mods.farmersdelight.Campfire.removeRecipesByOutput(<minecraft:cooked_beef>);
// mods.farmersdelight.CookingPot.removeRecipesByOutput(<minecraft:beetroot_soup>);
// mods.farmersdelight.CuttingBoard.removeRecipesByOutput(<minecraft:dye:15>);
//
// 字符串版删除模板。
// String overload templates for removal.
// mods.farmersdelight.Campfire.removeRecipesByOutput("minecraft:cooked_beef");
// mods.farmersdelight.CookingPot.removeRecipesByOutput("minecraft:beetroot_soup");
// mods.farmersdelight.CuttingBoard.removeRecipesByOutput("minecraft:fish@1");
