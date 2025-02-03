package me.hsgamer.cachy.holder;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import me.hsgamer.cachy.Cachy;
import me.hsgamer.cachy.manager.DataStorageManager;
import me.hsgamer.topper.agent.holder.AgentDataHolder;
import me.hsgamer.topper.agent.storage.StorageAgent;
import me.hsgamer.topper.spigot.agent.runnable.SpigotRunnableAgent;
import me.hsgamer.topper.storage.core.DataStorage;

import java.util.UUID;

public class CacheHolder extends AgentDataHolder<UUID, String> {
    public CacheHolder(Cachy plugin, String name) {
        super(name);

        DataStorage<UUID, String> storage = plugin.get(DataStorageManager.class).buildStorage(name, 65536);
        StorageAgent<UUID, String> storageAgent = new StorageAgent<>(this, storage);
        addAgent(storageAgent);
        addEntryAgent(storageAgent);
        addAgent(new SpigotRunnableAgent(storageAgent, AsyncScheduler.get(plugin), 20));
    }
}
