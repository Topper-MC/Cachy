package me.hsgamer.cachy.hook;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.clip.placeholderapi.PlaceholderAPI;
import me.hsgamer.cachy.Cachy;
import me.hsgamer.cachy.builder.ValueProviderBuilder;
import me.hsgamer.cachy.manager.CacheQueryForward;
import me.hsgamer.cachy.manager.CacheQueryManager;
import me.hsgamer.topper.spigot.query.forward.placeholderapi.PlaceholderQueryForwarder;
import me.hsgamer.topper.spigot.value.placeholderapi.PlaceholderValueProvider;
import org.bukkit.entity.Player;

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

            PlaceholderQueryForwarder<CacheQueryForward.Context> forwarder = new PlaceholderQueryForwarder<>();
            plugin.get(CacheQueryForward.class).addForwarder(forwarder);
            disableTasks.add(forwarder::unregister);

            Runnable removeParse = plugin.get(CacheQueryManager.class).addParseFunction(PlaceholderAPI::setBracketPlaceholders);
            disableTasks.add(removeParse);
        }
    }

    @Override
    public void disable() {
        disableTasks.forEach(Runnable::run);
        disableTasks.clear();
    }
}
