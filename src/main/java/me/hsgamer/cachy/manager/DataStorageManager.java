package me.hsgamer.cachy.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.cachy.Cachy;
import me.hsgamer.cachy.config.MainConfig;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.database.client.sql.java.JavaSqlClient;
import me.hsgamer.topper.storage.core.DataStorage;
import me.hsgamer.topper.storage.sql.config.SqlDatabaseConfig;
import me.hsgamer.topper.storage.sql.converter.StringSqlValueConverter;
import me.hsgamer.topper.storage.sql.converter.UUIDSqlValueConverter;
import me.hsgamer.topper.storage.sql.core.SqlDatabaseSetting;
import me.hsgamer.topper.storage.sql.core.SqlValueConverter;
import me.hsgamer.topper.storage.sql.mysql.MySqlDataStorageSupplier;
import me.hsgamer.topper.storage.sql.sqlite.NewSqliteDataStorageSupplier;
import me.hsgamer.topper.storage.sql.sqlite.SqliteDataStorageSupplier;

import java.io.File;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Supplier;

public class DataStorageManager implements Loadable {
    private final Cachy plugin;

    public DataStorageManager(Cachy plugin) {
        this.plugin = plugin;
    }

    public DataStorage<UUID, String> buildStorage(String name) {
        String type = plugin.get(MainConfig.class).getStorageType();
        Supplier<File> baseFolder = () -> {
            File file = new File(plugin.getDataFolder(), "data");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file;
        };
        Supplier<SqlDatabaseSetting> sqlDatabaseSetting = () -> new SqlDatabaseConfig("cachy", new BukkitConfig(new File(plugin.getDataFolder(), "database.yml")));
        SqlValueConverter<UUID> sqlKeyConverter = new UUIDSqlValueConverter("uuid");
        SqlValueConverter<String> sqlValueConverter = new StringSqlValueConverter("value", "TEXT");
        switch (type.toLowerCase(Locale.ROOT)) {
            case "mysql": {
                MySqlDataStorageSupplier supplier = new MySqlDataStorageSupplier(sqlDatabaseSetting.get(), JavaSqlClient::new);
                return supplier.getStorage(name, sqlKeyConverter, sqlValueConverter);
            }
            case "new-sqlite": {
                SqliteDataStorageSupplier supplier = new NewSqliteDataStorageSupplier(baseFolder.get(), sqlDatabaseSetting.get(), JavaSqlClient::new);
                return supplier.getStorage(name, sqlKeyConverter, sqlValueConverter);
            }
            default: {
                SqliteDataStorageSupplier supplier = new SqliteDataStorageSupplier(baseFolder.get(), sqlDatabaseSetting.get(), JavaSqlClient::new);
                return supplier.getStorage(name, sqlKeyConverter, sqlValueConverter);
            }
        }
    }
}
