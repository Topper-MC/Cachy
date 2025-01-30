package me.hsgamer.cachy.config;

import me.hsgamer.hscore.config.annotation.ConfigPath;

public interface MainConfig {
    @ConfigPath("storage-type")
    default String getStorageType() {
        return "flat";
    }
}
