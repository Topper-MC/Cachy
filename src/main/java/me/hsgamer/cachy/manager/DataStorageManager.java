package me.hsgamer.cachy.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.cachy.Cachy;
import me.hsgamer.cachy.config.MainConfig;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.topper.spigot.storage.simple.SpigotDataStorageBuilder;
import me.hsgamer.topper.storage.core.DataStorage;
import me.hsgamer.topper.storage.simple.builder.DataStorageBuilder;
import me.hsgamer.topper.storage.simple.config.DatabaseConfig;
import me.hsgamer.topper.storage.simple.converter.StringConverter;
import me.hsgamer.topper.storage.simple.converter.UUIDConverter;
import me.hsgamer.topper.storage.simple.setting.DataStorageSetting;
import me.hsgamer.topper.storage.simple.supplier.DataStorageSupplier;

import java.io.File;
import java.util.UUID;

public class DataStorageManager implements Loadable {
    private final Cachy plugin;
    private DataStorageSupplier supplier;

    public DataStorageManager(Cachy plugin) {
        this.plugin = plugin;
    }

    public DataStorage<UUID, String> buildStorage(String name, int valueSize) {
        return this.supplier.getStorage(name, new UUIDConverter("uuid"), new StringConverter("value", true, valueSize));
    }

    @Override
    public void enable() {
        DataStorageBuilder builder = new DataStorageBuilder();
        SpigotDataStorageBuilder.register(builder);

        this.supplier = builder.buildSupplier(plugin.get(MainConfig.class).getStorageType(), new DataStorageSetting() {
            @Override
            public DatabaseConfig getDatabaseSetting() {
                return new DatabaseConfig("cachy", new BukkitConfig(new File(plugin.getDataFolder(), "database.yml")));
            }

            @Override
            public File getBaseFolder() {
                return new File(plugin.getDataFolder(), "data");
            }
        });
        this.supplier.enable();
    }

    @Override
    public void disable() {
        if (this.supplier != null) {
            this.supplier.disable();
        }
    }
}
