package me.hsgamer.cachy.holder;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import me.hsgamer.cachy.Cachy;
import me.hsgamer.cachy.holder.agent.SyncAgent;
import me.hsgamer.cachy.holder.agent.UpdateAgent;
import me.hsgamer.cachy.manager.DataStorageManager;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.agent.core.AgentHolder;
import me.hsgamer.topper.agent.core.DataEntryAgent;
import me.hsgamer.topper.agent.storage.StorageAgent;
import me.hsgamer.topper.data.simple.SimpleDataHolder;
import me.hsgamer.topper.spigot.agent.runnable.SpigotRunnableAgent;
import me.hsgamer.topper.storage.core.DataStorage;

import java.math.BigDecimal;
import java.util.*;

public class CacheHolder extends SimpleDataHolder<UUID, String> implements AgentHolder<UUID, String> {
    private final List<Agent> agents;
    private final List<DataEntryAgent<UUID, String>> entryAgents;
    private final UpdateAgent updateAgent;
    private final String name;

    public CacheHolder(Cachy plugin, String name, Map<String, Object> map) {
        this.name = name;
        this.agents = new ArrayList<>();
        this.entryAgents = new ArrayList<>();
        DataStorage<UUID, String> storage = plugin.get(DataStorageManager.class).buildStorage(name);
        StorageAgent<UUID, String> storageAgent = new StorageAgent<>(storage);
        agents.add(storageAgent);
        entryAgents.add(storageAgent);
        agents.add(new SpigotRunnableAgent(storageAgent, AsyncScheduler.get(plugin), 20));
        agents.add(storageAgent.getLoadAgent(this));

        this.updateAgent = new UpdateAgent(plugin, this, map);
        agents.add(updateAgent);

        Optional<Long> syncPeriod = Optional.ofNullable(map.get("sync"))
                .map(String::valueOf)
                .flatMap(Validate::getNumber)
                .map(BigDecimal::longValue);
        if (syncPeriod.isPresent()) {
            SyncAgent syncAgent = new SyncAgent(this, storage);
            agents.add(syncAgent);
            agents.add(new SpigotRunnableAgent(syncAgent, AsyncScheduler.get(plugin), syncPeriod.get()));
        }
    }

    @Override
    public String getDefaultValue() {
        return "";
    }

    public UpdateAgent getUpdateAgent() {
        return updateAgent;
    }

    @Override
    public List<Agent> getAgents() {
        return agents;
    }

    @Override
    public List<DataEntryAgent<UUID, String>> getEntryAgents() {
        return entryAgents;
    }

    public String getName() {
        return name;
    }
}
