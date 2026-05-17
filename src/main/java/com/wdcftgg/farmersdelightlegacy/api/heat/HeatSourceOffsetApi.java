package com.wdcftgg.farmersdelightlegacy.api.heat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Extension API for heat source offset checks.
 * <p>
 * Use this when a support block below cookware is not a heat source itself and lookup should continue one block lower.
 */
public final class HeatSourceOffsetApi {
    private static final Map<String, IHeatSourceOffsetPredicate> OFFSET_PREDICATES = new LinkedHashMap<>();

    private HeatSourceOffsetApi() {
    }

    /**
     * Registers a heat-source offset predicate.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     * @param predicate The predicate callback to register or evaluate.
     */
    public static void registerOffsetPredicate(String key, IHeatSourceOffsetPredicate predicate) {
        if (key != null && predicate != null) {
            synchronized (OFFSET_PREDICATES) {
                OFFSET_PREDICATES.put(key, predicate);
            }
        }
    }

    /**
     * Unregisters a heat-source offset predicate.
     *
     * @param key The unique recipe or predicate id used by the backing manager.
     */
    public static void unregisterOffsetPredicate(String key) {
        if (key != null) {
            synchronized (OFFSET_PREDICATES) {
                OFFSET_PREDICATES.remove(key);
            }
        }
    }

    /**
     * Checks whether heat-source lookup should continue one block lower.
     *
     * @param world The world where the predicate or query is evaluated.
     * @param pos The block position being evaluated.
     * @param state The block state being evaluated.
     * @return The result produced by this API method.
     */
    public static boolean shouldOffsetDown(World world, BlockPos pos, IBlockState state) {
        IHeatSourceOffsetPredicate[] predicates;
        synchronized (OFFSET_PREDICATES) {
            predicates = OFFSET_PREDICATES.values().toArray(new IHeatSourceOffsetPredicate[0]);
        }
        for (IHeatSourceOffsetPredicate predicate : predicates) {
            if (predicate.shouldOffsetDown(world, pos, state)) {
                return true;
            }
        }
        return false;
    }
}
