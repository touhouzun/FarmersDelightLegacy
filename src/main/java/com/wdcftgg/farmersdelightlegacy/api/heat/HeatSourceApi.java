package com.wdcftgg.farmersdelightlegacy.api.heat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Extension API for direct heat source checks.
 * <p>
 * Addons can register {@link IHeatSourcePredicate} callbacks during initialization so their own blocks participate in
 * the Farmers Delight Legacy direct heat source lookup.
 */
public final class HeatSourceApi {
    private static final Map<String, IHeatSourcePredicate> DIRECT_HEAT_SOURCE_PREDICATES = new LinkedHashMap<>();

    private HeatSourceApi() {
    }

    /**
     * Registers a direct heat source predicate.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param predicate The predicate callback to register or evaluate.
     */
    public static void registerDirectHeatSourcePredicate(String key, IHeatSourcePredicate predicate) {
        if (key != null && predicate != null) {
            synchronized (DIRECT_HEAT_SOURCE_PREDICATES) {
                DIRECT_HEAT_SOURCE_PREDICATES.put(key, predicate);
            }
        }
    }

    /**
     * Unregisters a direct heat source predicate.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     */
    public static void unregisterDirectHeatSourcePredicate(String key) {
        if (key != null) {
            synchronized (DIRECT_HEAT_SOURCE_PREDICATES) {
                DIRECT_HEAT_SOURCE_PREDICATES.remove(key);
            }
        }
    }

    /**
     * Checks whether any registered predicate accepts a direct heat source.
     *
     * @param world The world where the predicate or query is evaluated.
     * @param pos The block position being evaluated.
     * @param state The block state being evaluated.
     * @return The result produced by this API method.
     */
    public static boolean isRegisteredAsDirectHeatSource(World world, BlockPos pos, IBlockState state) {
        IHeatSourcePredicate[] predicates;
        synchronized (DIRECT_HEAT_SOURCE_PREDICATES) {
            predicates = DIRECT_HEAT_SOURCE_PREDICATES.values().toArray(new IHeatSourcePredicate[0]);
        }
        for (IHeatSourcePredicate predicate : predicates) {
            if (predicate.isHeatSource(world, pos, state)) {
                return true;
            }
        }
        return false;
    }
}

