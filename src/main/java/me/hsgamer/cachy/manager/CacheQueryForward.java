package me.hsgamer.cachy.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.cachy.Cachy;
import me.hsgamer.topper.query.core.QueryResult;
import me.hsgamer.topper.query.forward.QueryForward;
import me.hsgamer.topper.query.forward.QueryForwardContext;
import me.hsgamer.topper.spigot.query.forward.plugin.PluginContext;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class CacheQueryForward extends QueryForward<OfflinePlayer, CacheQueryForward.Context> implements Loadable {
    private final Cachy plugin;

    public CacheQueryForward(Cachy plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        addContext(new Context() {
            @Override
            public Plugin getPlugin() {
                return plugin;
            }

            @Override
            public String getName() {
                return "cachy";
            }

            @Override
            public BiFunction<@Nullable OfflinePlayer, @NotNull String, @NotNull QueryResult> getQuery() {
                return plugin.get(CacheQueryManager.class);
            }
        });
    }

    public interface Context extends PluginContext, QueryForwardContext<OfflinePlayer> {

    }
}
