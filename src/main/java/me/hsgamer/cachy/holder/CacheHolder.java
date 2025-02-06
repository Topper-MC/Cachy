package me.hsgamer.cachy.holder;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import me.hsgamer.cachy.Cachy;
import me.hsgamer.cachy.holder.agent.UpdateAgent;
import me.hsgamer.cachy.manager.DataStorageManager;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.topper.agent.holder.AgentDataHolder;
import me.hsgamer.topper.agent.storage.StorageAgent;
import me.hsgamer.topper.spigot.agent.runnable.SpigotRunnableAgent;
import me.hsgamer.topper.storage.core.DataStorage;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CacheHolder extends AgentDataHolder<UUID, String> {
    private final UpdateAgent updateAgent;

    public CacheHolder(Cachy plugin, String name, Map<String, Object> map) {
        super(name);

        int valueSize = Optional.ofNullable(map.get("value-size"))
                .map(String::valueOf)
                .flatMap(Validate::getNumber)
                .map(Number::intValue)
                .orElse(65536);
        DataStorage<UUID, String> storage = plugin.get(DataStorageManager.class).buildStorage(name, valueSize);
        StorageAgent<UUID, String> storageAgent = new StorageAgent<>(this, storage);
        addAgent(storageAgent);
        addEntryAgent(storageAgent);
        addAgent(new SpigotRunnableAgent(storageAgent, AsyncScheduler.get(plugin), 20));

        this.updateAgent = new UpdateAgent(plugin, this, map);
        addAgent(updateAgent);
    }

    @Override
    protected String getDefaultValue() {
        return "";
    }

    public UpdateAgent getUpdateAgent() {
        return updateAgent;
    }
}
