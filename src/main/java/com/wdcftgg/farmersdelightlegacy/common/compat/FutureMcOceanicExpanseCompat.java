package com.wdcftgg.farmersdelightlegacy.common.compat;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.common.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;

import java.io.File;

public final class FutureMcOceanicExpanseCompat {

    private static final String FUTURE_MC_MOD_ID = "futuremc";
    private static final String FUTURE_MC_CONFIG_PATH = "futuremc/futuremc.cfg";
    private static final String GENERAL_CATEGORY = "general";
    private static final String UPDATE_AQUATIC_CATEGORY = "update aquatic";
    private static final String OCEANIC_EXPANSE_KEY = "Oceanic Expanse Compatibility";

    private FutureMcOceanicExpanseCompat() {
    }

    public static void syncFutureMcConfig() {
        if (!Loader.isModLoaded(FUTURE_MC_MOD_ID) || !Configuration.skipFutureMcOceanicExpanseCheck) {
            return;
        }

        File configDirectory = Loader.instance().getConfigDir();
        if (configDirectory == null) {
            return;
        }

        File futureMcConfigFile = new File(configDirectory, FUTURE_MC_CONFIG_PATH);
        File parentDirectory = futureMcConfigFile.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists() && !parentDirectory.mkdirs()) {
            FarmersDelightLegacy.LOGGER.warn("无法创建 Future MC 配置目录，已跳过去皮原木兼容配置调整：{}", parentDirectory);
            return;
        }

        net.minecraftforge.common.config.Configuration futureMcConfig = new net.minecraftforge.common.config.Configuration(futureMcConfigFile);
        futureMcConfig.load();
        Property oceanicExpanseProperty = futureMcConfig.get(GENERAL_CATEGORY + "." + UPDATE_AQUATIC_CATEGORY, OCEANIC_EXPANSE_KEY, true,
                "Disables ocean features in Future MC if Oceanic Expanse is loaded and has equivalent features.");
        if (!oceanicExpanseProperty.getBoolean()) {
            futureMcConfig.save();
            return;
        }

        oceanicExpanseProperty.set(false);
        futureMcConfig.save();
        FarmersDelightLegacy.LOGGER.info("已关闭 Future MC 的 Oceanic Expanse 兼容开关，以保留 stripped log 注册。");
    }
}
