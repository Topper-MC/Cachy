package me.hsgamer.cachy.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.cachy.Cachy;
import me.hsgamer.cachy.config.MainConfig;
import me.hsgamer.cachy.holder.CacheHolder;

import java.util.*;

public class CacheHolderManager implements Loadable {
    private final Cachy plugin;
    private final Map<String, CacheHolder> holders = new HashMap<>();

    public CacheHolderManager(Cachy plugin) {
        this.plugin = plugin;
    }

    public Optional<CacheHolder> getHolder(String name) {
        return Optional.ofNullable(holders.get(name));
    }

    public Set<String> getHolderNames() {
        return Collections.unmodifiableSet(holders.keySet());
    }

    @Override
    public void enable() {
        plugin.get(MainConfig.class).getHolders().forEach((name, map) -> {
            CacheHolder holder = new CacheHolder(plugin, name, map);
            holder.register();
            holders.put(name, holder);
        });
    }

    @Override
    public void disable() {
        holders.values().forEach(CacheHolder::unregister);
        holders.clear();
    }
}
