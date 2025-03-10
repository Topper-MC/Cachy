package me.hsgamer.cachy;

import io.github.projectunified.minelib.plugin.base.BasePlugin;
import me.hsgamer.cachy.builder.ValueProviderBuilder;
import me.hsgamer.cachy.config.MainConfig;
import me.hsgamer.cachy.hook.HookSystem;
import me.hsgamer.cachy.manager.CacheHolderManager;
import me.hsgamer.cachy.manager.CacheQueryForward;
import me.hsgamer.cachy.manager.CacheQueryManager;
import me.hsgamer.cachy.manager.DataStorageManager;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;

import java.util.Arrays;
import java.util.List;

public final class Cachy extends BasePlugin {
    @Override
    protected List<Object> getComponents() {
        return Arrays.asList(
                ConfigGenerator.newInstance(MainConfig.class, new BukkitConfig(this)),

                new ValueProviderBuilder(),

                new HookSystem(this),

                new DataStorageManager(this),
                new CacheHolderManager(this),
                new CacheQueryManager(this),
                new CacheQueryForward(this)
        );
    }
}
