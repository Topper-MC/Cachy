package me.hsgamer.cachy.hook;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hsgamer.cachy.Cachy;
import me.hsgamer.cachy.builder.ValueProviderBuilder;
import me.hsgamer.cachy.manager.CacheQueryManager;
import me.hsgamer.topper.spigot.value.placeholderapi.PlaceholderValueProvider;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class HookSystem implements Loadable {
    private final Cachy plugin;
    private final List<Runnable> disableTasks = new ArrayList<>();

    public HookSystem(Cachy plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            plugin.get(ValueProviderBuilder.class).register(input -> {
                String placeholder = Optional.ofNullable(input.data.get("placeholder")).map(Object::toString).orElse("");
                return new PlaceholderValueProvider(placeholder, true)
                        .<Player>keyMapper(player -> player)
                        .thenApply(output -> Objects.equals(placeholder, output) ? null : output);
            }, "placeholder", "placeholderapi", "papi");
            PlaceholderExpansion expansion = new PlaceholderExpansion() {
                @Override
                public @NotNull String getIdentifier() {
                    return "cachy";
                }

                @Override
                public @NotNull String getAuthor() {
                    return String.join(", ", plugin.getDescription().getAuthors());
                }

                @Override
                public @NotNull String getVersion() {
                    return plugin.getDescription().getVersion();
                }

                @Override
                public boolean persist() {
                    return true;
                }

                @Override
                public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
                    return plugin.get(CacheQueryManager.class).get(player, params);
                }
            };
            expansion.register();
            disableTasks.add(expansion::unregister);
        }
    }

    @Override
    public void disable() {
        disableTasks.forEach(Runnable::run);
        disableTasks.clear();
    }
}
