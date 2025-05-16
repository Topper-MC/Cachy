package me.hsgamer.cachy.holder.provider;

import me.hsgamer.topper.value.core.ValueProvider;
import me.hsgamer.topper.value.core.ValueWrapper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class PlayerValueProvider implements ValueProvider<Player, String> {
    private final DataType dataType;

    private PlayerValueProvider(DataType dataType) {
        this.dataType = dataType;
    }

    public static PlayerValueProvider of(Map<String, Object> map) {
        DataType dataType = Optional.ofNullable(map.get("data-type"))
                .map(Object::toString)
                .map(String::toUpperCase)
                .map(s -> {
                    try {
                        return DataType.valueOf(s);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .orElse(DataType.NONE);
        return new PlayerValueProvider(dataType);
    }

    @Override
    public @NotNull ValueWrapper<String> apply(@NotNull Player player) {
        switch (dataType) {
            case NAME:
                return ValueWrapper.handled(player.getName());
            case DISPLAY_NAME:
                return ValueWrapper.handled(player.getDisplayName());
            case XP:
                return ValueWrapper.handled(String.valueOf(player.getTotalExperience()));
            case LEVEL:
                return ValueWrapper.handled(String.valueOf(player.getLevel()));
            default:
                return ValueWrapper.notHandled();
        }
    }

    public enum DataType {
        NAME,
        DISPLAY_NAME,
        XP,
        LEVEL,

        NONE
    }
}
