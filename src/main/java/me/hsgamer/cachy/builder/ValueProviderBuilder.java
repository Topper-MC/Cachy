package me.hsgamer.cachy.builder;

import me.hsgamer.cachy.holder.provider.PlayerValueProvider;
import me.hsgamer.hscore.builder.FunctionalMassBuilder;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.topper.spigot.value.statistic.StatisticValueProvider;
import me.hsgamer.topper.value.core.ValueProvider;
import org.bukkit.entity.Player;

import java.util.*;

public class ValueProviderBuilder extends FunctionalMassBuilder<ValueProviderBuilder.Input, ValueProvider<Player, String>> {
    public ValueProviderBuilder() {
        register(input -> {
            String statistic = Optional.ofNullable(input.data.get("statistic")).map(Objects::toString).orElse(null);
            List<String> materials = Optional.ofNullable(input.data.get("material")).map(CollectionUtils::createStringListFromObject).orElse(Collections.emptyList());
            List<String> entityTypes = Optional.ofNullable(input.data.get("entity-type")).map(CollectionUtils::createStringListFromObject).orElse(Collections.emptyList());
            return StatisticValueProvider.fromRaw(statistic, materials, entityTypes).thenApply(Object::toString);
        }, "statistic");
        register(input -> PlayerValueProvider.of(input.data), "player");
    }

    @Override
    protected String getType(Input input) {
        return input.type;
    }

    public static class Input {
        public final String type;
        public final Map<String, Object> data;

        public Input(String type, Map<String, Object> data) {
            this.type = type;
            this.data = data;
        }
    }
}
