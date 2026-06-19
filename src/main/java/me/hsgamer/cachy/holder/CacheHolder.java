package me.hsgamer.cachy.holder;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import io.github.projectunified.minelib.scheduler.global.GlobalScheduler;
import me.hsgamer.cachy.Cachy;
import me.hsgamer.cachy.builder.ValueProviderBuilder;
import me.hsgamer.cachy.manager.DataStorageManager;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.agent.core.AgentHolder;
import me.hsgamer.topper.agent.core.DataEntryAgent;
import me.hsgamer.topper.agent.storage.StorageAgent;
import me.hsgamer.topper.agent.update.UpdateAgent;
import me.hsgamer.topper.data.simple.SimpleDataHolder;
import me.hsgamer.topper.spigot.agent.runnable.SpigotRunnableAgent;
import me.hsgamer.topper.storage.core.DataStorage;
import me.hsgamer.topper.value.core.ValueProvider;
import me.hsgamer.topper.value.core.ValueWrapper;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;

public class CacheHolder extends SimpleDataHolder<UUID, String> implements AgentHolder<UUID, String> {
    private final List<Agent> agents;
    private final List<DataEntryAgent<UUID, String>> entryAgents;
    private final String name;

    public CacheHolder(Cachy plugin, String name, Map<String, Object> map) {
        this.name = name;
        this.agents = new ArrayList<>();
        this.entryAgents = new ArrayList<>();

        Map<String, Object> periodMap = Optional.ofNullable(map.get("period"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .orElseGet(Collections::emptyMap);
        long updatePeriod = Optional.ofNullable(periodMap.get("update"))
                .map(Objects::toString)
                .flatMap(Validate::getNumber)
                .map(BigDecimal::longValue)
                .orElse(0L);
        long setPeriod = Optional.ofNullable(periodMap.get("set"))
                .map(Objects::toString)
                .flatMap(Validate::getNumber)
                .map(BigDecimal::longValue)
                .orElse(20L);
        long savePeriod = Optional.ofNullable(periodMap.get("save"))
                .map(Objects::toString)
                .flatMap(Validate::getNumber)
                .map(BigDecimal::longValue)
                .orElse(20L);
        Map<String, Object> amountMap = Optional.ofNullable(map.get("amount"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .orElseGet(Collections::emptyMap);
        int updateAmount = Optional.ofNullable(amountMap.get("update"))
                .map(Objects::toString)
                .flatMap(Validate::getNumber)
                .map(BigDecimal::intValue)
                .orElse(10);
        int saveAmount = Optional.ofNullable(amountMap.get("save"))
                .map(Objects::toString)
                .flatMap(Validate::getNumber)
                .map(BigDecimal::intValue)
                .orElse(10);
        boolean isAsync = Optional.ofNullable(map.get("async"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(false);
        boolean showErrors = Optional.ofNullable(map.get("show-errors"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(false);
        Optional<Long> syncPeriod = Optional.ofNullable(map.get("sync"))
                .map(String::valueOf)
                .flatMap(Validate::getNumber)
                .map(BigDecimal::longValue);
        String type = Objects.toString(map.get("type"), "");

        DataStorage<UUID, String> storage = plugin.get(DataStorageManager.class).buildStorage(name);
        StorageAgent<UUID, String> storageAgent = new StorageAgent<>(storage);
        Agent loadAgent = storageAgent.getLoadAgent(this);

        if (syncPeriod.isPresent()) {
            agents.add(new SpigotRunnableAgent(loadAgent::start, AsyncScheduler.get(plugin), syncPeriod.get()));
        } else {
            ValueProvider<UUID, String> valueProvider = plugin.get(ValueProviderBuilder.class).build(new ValueProviderBuilder.Input(type, map)).orElseGet(() -> {
                plugin.getLogger().warning("No value provider found for " + type + " in " + name);
                return ValueProvider.empty();
            }).beforeApply(Bukkit::getPlayer);

            UpdateAgent<UUID, String> updateAgent = new UpdateAgent<>(this, valueProvider);
            if (showErrors) {
                updateAgent.setErrorHandler((uuid, valueWrapper) -> {
                    if (valueWrapper.state == ValueWrapper.State.ERROR) {
                        plugin.getLogger().log(Level.WARNING, "Error on getting value for " + name + " from " + uuid + " - " + valueWrapper.errorMessage, valueWrapper.throwable);
                    }
                });
            }

            entryAgents.add(updateAgent);
            agents.add(new SpigotRunnableAgent(updateAgent.getUpdateRunnable(updateAmount), isAsync ? AsyncScheduler.get(plugin) : GlobalScheduler.get(plugin), updatePeriod));
            agents.add(new SpigotRunnableAgent(updateAgent.getSetRunnable(), AsyncScheduler.get(plugin), setPeriod));

            storageAgent.setMaxEntryPerCall(saveAmount);
            agents.add(storageAgent);
            entryAgents.add(storageAgent);
            agents.add(new SpigotRunnableAgent(storageAgent, AsyncScheduler.get(plugin), savePeriod));
            agents.add(loadAgent);
        }
    }

    @Override
    public String getDefaultValue() {
        return "";
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
