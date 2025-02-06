package me.hsgamer.cachy.holder;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import io.github.projectunified.minelib.scheduler.entity.EntityScheduler;
import me.hsgamer.cachy.Cachy;
import me.hsgamer.cachy.builder.ValueProviderBuilder;
import me.hsgamer.cachy.manager.DataStorageManager;
import me.hsgamer.topper.agent.holder.AgentDataHolder;
import me.hsgamer.topper.agent.storage.StorageAgent;
import me.hsgamer.topper.agent.update.UpdateAgent;
import me.hsgamer.topper.spigot.agent.runnable.SpigotRunnableAgent;
import me.hsgamer.topper.storage.core.DataStorage;
import me.hsgamer.topper.value.core.ValueProvider;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class CacheHolder extends AgentDataHolder<UUID, String> {
    public CacheHolder(Cachy plugin, String name, Map<String, Object> map) {
        super(name);

        DataStorage<UUID, String> storage = plugin.get(DataStorageManager.class).buildStorage(name, 65536);
        StorageAgent<UUID, String> storageAgent = new StorageAgent<>(this, storage);
        addAgent(storageAgent);
        addEntryAgent(storageAgent);
        addAgent(new SpigotRunnableAgent(storageAgent, AsyncScheduler.get(plugin), 20));

        String type = Objects.toString(map.get("type"), "readonly");
        if (!type.equalsIgnoreCase("readonly")) {
            ValueProvider<Player, String> valueProvider = plugin.get(ValueProviderBuilder.class).build(new ValueProviderBuilder.Input(type, map)).orElseGet(() -> {
                plugin.getLogger().warning("No value provider found for " + type + " in " + name);
                return ValueProvider.empty();
            });
            boolean isAsync = Optional.ofNullable(map.get("async"))
                    .map(String::valueOf)
                    .map(Boolean::parseBoolean)
                    .orElse(false);
            boolean showErrors = Optional.ofNullable(map.get("show-errors"))
                    .map(String::valueOf)
                    .map(Boolean::parseBoolean)
                    .orElse(false);
            UpdateAgent<UUID, String> updateAgent = new UpdateAgent<>(this, uuid -> {
                Player player = plugin.getServer().getPlayer(uuid);
                if (player == null) {
                    return CompletableFuture.completedFuture(Optional.empty());
                }
                return CompletableFuture.supplyAsync(() -> valueProvider.apply(player).asOptional((message, throwable) -> {
                    if (showErrors) {
                        plugin.getLogger().log(Level.WARNING, "An error occurred while getting the value for " + player.getName() + " in " + name + " - " + message, throwable);
                    }
                }), (isAsync ? AsyncScheduler.get(plugin) : EntityScheduler.get(plugin, player)).getExecutor());
            });
            addAgent(new SpigotRunnableAgent(updateAgent, AsyncScheduler.get(plugin), 20));
        }
    }

    @Override
    protected String getDefaultValue() {
        return "";
    }
}
