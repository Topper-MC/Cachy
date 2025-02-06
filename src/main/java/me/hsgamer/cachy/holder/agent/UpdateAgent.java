package me.hsgamer.cachy.holder.agent;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import io.github.projectunified.minelib.scheduler.entity.EntityScheduler;
import me.hsgamer.cachy.Cachy;
import me.hsgamer.cachy.builder.ValueProviderBuilder;
import me.hsgamer.cachy.holder.CacheHolder;
import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.value.core.ValueProvider;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class UpdateAgent implements Agent {
    private final Cachy plugin;
    private final CacheHolder holder;
    private final ValueProvider<Player, String> valueProvider;
    private final boolean isAsync;
    private final boolean showErrors;

    public UpdateAgent(Cachy plugin, CacheHolder holder, Map<String, Object> map) {
        this.plugin = plugin;
        this.holder = holder;

        String type = Objects.toString(map.get("type"), "readonly");
        this.valueProvider = type.equalsIgnoreCase("readonly") ? ValueProvider.empty() : plugin.get(ValueProviderBuilder.class).build(new ValueProviderBuilder.Input(type, map)).orElseGet(() -> {
            plugin.getLogger().warning("No value provider found for " + type + " in " + holder.getName());
            return ValueProvider.empty();
        });

        this.isAsync = Optional.ofNullable(map.get("async"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(false);
        this.showErrors = Optional.ofNullable(map.get("show-errors"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    public void update(Player player) {
        CompletableFuture
                .supplyAsync(() -> valueProvider.apply(player), (isAsync ? AsyncScheduler.get(plugin) : EntityScheduler.get(plugin, player)).getExecutor())
                .thenApply(wrapper -> wrapper.asOptional((message, throwable) -> {
                    if (showErrors) {
                        plugin.getLogger().log(Level.WARNING, "An error occurred while getting the value for " + player.getName() + " in " + holder.getName() + " - " + message, throwable);
                    }
                }))
                .thenAcceptAsync(value -> value.ifPresent(string -> holder.getOrCreateEntry(player.getUniqueId()).setValue(string)));
    }
}
