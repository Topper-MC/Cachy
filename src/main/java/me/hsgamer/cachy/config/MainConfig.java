package me.hsgamer.cachy.config;

import me.hsgamer.cachy.config.converter.StringObjectValueMapConverter;
import me.hsgamer.hscore.config.annotation.ConfigPath;

import java.util.Collections;
import java.util.Map;

public interface MainConfig {
    @ConfigPath(value = "holders", converter = StringObjectValueMapConverter.class)
    default Map<String, Map<String, Object>> getHolders() {
        return Collections.emptyMap();
    }

    @ConfigPath("storage-type")
    default String getStorageType() {
        return "flat";
    }

    @ConfigPath({"task", "update", "period"})
    default long getTaskUpdatePeriod() {
        return 10;
    }
}
