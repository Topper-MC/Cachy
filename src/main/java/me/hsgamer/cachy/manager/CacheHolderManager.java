package me.hsgamer.cachy.manager;

import com.google.common.collect.ImmutableList;
import io.github.projectunified.minelib.plugin.base.Loadable;
import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import io.github.projectunified.minelib.scheduler.common.task.Task;
import me.hsgamer.cachy.Cachy;
import me.hsgamer.cachy.config.MainConfig;
import me.hsgamer.cachy.holder.CacheHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class CacheHolderManager implements Loadable {
    private final Cachy plugin;
    private final Map<String, CacheHolder> holders = new HashMap<>();
    private Task updateTask;

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
        this.updateTask = AsyncScheduler.get(plugin).runTimer(() -> {
            for (Player player : ImmutableList.copyOf(Bukkit.getOnlinePlayers())) {
                holders.values().forEach(holder -> holder.getUpdateAgent().update(player));
            }
        }, 0, 0);
    }

    @Override
    public void disable() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        holders.values().forEach(CacheHolder::unregister);
        holders.clear();
    }
}
