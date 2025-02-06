package me.hsgamer.cachy.holder.agent;

import me.hsgamer.cachy.holder.CacheHolder;
import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.storage.core.DataStorage;

import java.util.UUID;

public class SyncAgent implements Agent, Runnable {
    private final CacheHolder holder;
    private final DataStorage<UUID, String> storage;

    public SyncAgent(CacheHolder holder, DataStorage<UUID, String> storage) {
        this.holder = holder;
        this.storage = storage;
    }

    @Override
    public void run() {
        storage.load().forEach((uuid, string) -> holder.getOrCreateEntry(uuid).setValue(string, false));
    }
}
