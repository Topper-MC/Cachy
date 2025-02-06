package me.hsgamer.cachy.builder;

import me.hsgamer.hscore.builder.FunctionalMassBuilder;
import me.hsgamer.topper.spigot.value.statistic.StatisticValueProvider;
import me.hsgamer.topper.value.core.ValueProvider;
import org.bukkit.entity.Player;

import java.util.Map;

public class ValueProviderBuilder extends FunctionalMassBuilder<ValueProviderBuilder.Input, ValueProvider<Player, String>> {
    public ValueProviderBuilder() {
        register(input -> StatisticValueProvider.fromMap(input.data).thenApply(Object::toString), "statistic");
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
