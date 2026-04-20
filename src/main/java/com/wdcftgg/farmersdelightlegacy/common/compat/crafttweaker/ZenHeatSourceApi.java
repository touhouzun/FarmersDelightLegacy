package com.wdcftgg.farmersdelightlegacy.common.compat.crafttweaker;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.api.heat.HeatSourceApi;
import com.wdcftgg.farmersdelightlegacy.api.heat.HeatSourceOffsetApi;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IWorld;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.farmersdelight.HeatSource")
public final class ZenHeatSourceApi {

    private ZenHeatSourceApi() {
    }

    @ZenMethod
    public static boolean addDirectHeatSourceBlock(String key, String blockId) {
        return addDirectHeatSourceBlockWithMeta(key, blockId, -1);
    }

    @ZenMethod
    public static boolean addDirectHeatSourceBlockWithMeta(String key, String blockId, int metadata) {
        Block block = CraftTweakerCompatHelper.blockOf(blockId);
        if (key == null || key.isEmpty() || block == null) {
            return false;
        }

        HeatSourceApi.registerDirectHeatSourcePredicate(key, (world, pos, state) -> matchesBlockAndMeta(state, block, metadata));
        return true;
    }

    @ZenMethod
    public static boolean addDirectHeatSourcePredicate(String key, ZenDirectHeatSourcePredicate predicate) {
        if (key == null || key.isEmpty() || predicate == null) {
            return false;
        }

        HeatSourceApi.registerDirectHeatSourcePredicate(key, (world, pos, state) -> invokeDirectPredicate(key, predicate, world, pos, state));
        return true;
    }

    @ZenMethod
    public static boolean removeDirectHeatSourceBlock(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        HeatSourceApi.unregisterDirectHeatSourcePredicate(key);
        return true;
    }

    @ZenMethod
    public static boolean addOffsetBlock(String key, String blockId) {
        return addOffsetBlockWithMeta(key, blockId, -1);
    }

    @ZenMethod
    public static boolean addOffsetBlockWithMeta(String key, String blockId, int metadata) {
        Block block = CraftTweakerCompatHelper.blockOf(blockId);
        if (key == null || key.isEmpty() || block == null) {
            return false;
        }

        HeatSourceOffsetApi.registerOffsetPredicate(key, (world, pos, state) -> matchesBlockAndMeta(state, block, metadata));
        return true;
    }

    @ZenMethod
    public static boolean addOffsetPredicate(String key, ZenHeatSourceOffsetPredicate predicate) {
        if (key == null || key.isEmpty() || predicate == null) {
            return false;
        }

        HeatSourceOffsetApi.registerOffsetPredicate(key, (world, pos, state) -> invokeOffsetPredicate(key, predicate, world, pos, state));
        return true;
    }

    @ZenMethod
    public static boolean removeOffsetBlock(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        HeatSourceOffsetApi.unregisterOffsetPredicate(key);
        return true;
    }

    @ZenMethod
    public static boolean isDirectHeatSourceAt(IWorld world, int x, int y, int z) {
        if (world == null) {
            return false;
        }
        World nativeWorld = CraftTweakerMC.getWorld(world);
        if (nativeWorld == null) {
            return false;
        }
        BlockPos pos = new BlockPos(x, y, z);
        if (!nativeWorld.isBlockLoaded(pos)) {
            return false;
        }
        IBlockState state = nativeWorld.getBlockState(pos);
        return HeatSourceApi.isRegisteredAsDirectHeatSource(nativeWorld, pos, state);
    }

    @ZenMethod
    public static boolean shouldOffsetDownAt(IWorld world, int x, int y, int z) {
        if (world == null) {
            return false;
        }
        World nativeWorld = CraftTweakerMC.getWorld(world);
        if (nativeWorld == null) {
            return false;
        }
        BlockPos pos = new BlockPos(x, y, z);
        if (!nativeWorld.isBlockLoaded(pos)) {
            return false;
        }
        IBlockState state = nativeWorld.getBlockState(pos);
        return HeatSourceOffsetApi.shouldOffsetDown(nativeWorld, pos, state);
    }

    private static boolean matchesBlockAndMeta(IBlockState state, Block expectedBlock, int expectedMeta) {
        if (state == null || state.getBlock() != expectedBlock) {
            return false;
        }
        return expectedMeta < 0 || expectedMeta == expectedBlock.getMetaFromState(state);
    }

    private static boolean invokeDirectPredicate(String key, ZenDirectHeatSourcePredicate predicate, World world, BlockPos pos, IBlockState state) {
        try {
            return invokeCallback(predicate, world, pos, state);
        } catch (Throwable throwable) {
            FarmersDelightLegacy.LOGGER.error("CraftTweaker 直接热源回调执行失败：{}", key, throwable);
            return false;
        }
    }

    private static boolean invokeOffsetPredicate(String key, ZenHeatSourceOffsetPredicate predicate, World world, BlockPos pos, IBlockState state) {
        try {
            return invokeCallback(predicate, world, pos, state);
        } catch (Throwable throwable) {
            FarmersDelightLegacy.LOGGER.error("CraftTweaker 热源偏移回调执行失败：{}", key, throwable);
            return false;
        }
    }

    private static boolean invokeCallback(ZenHeatSourcePredicateCallback callback, World world, BlockPos pos, IBlockState state) {
        ResourceLocation registryName = state.getBlock().getRegistryName();
        String blockId = registryName == null ? "" : registryName.toString();
        int metadata = state.getBlock().getMetaFromState(state);
        return callback.test(
                CraftTweakerMC.getIWorld(world),
                CraftTweakerMC.getIBlockPos(pos),
                CraftTweakerMC.getBlockState(state)
        );
    }
}

