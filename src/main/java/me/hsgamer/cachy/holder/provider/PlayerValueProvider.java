package me.hsgamer.cachy.holder.provider;

import me.hsgamer.topper.value.core.ValueProvider;
import me.hsgamer.topper.value.core.ValueWrapper;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

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
    public void accept(Player player, Consumer<ValueWrapper<String>> callback) {
        switch (dataType) {
            case NAME:
                callback.accept(ValueWrapper.handled(player.getName()));
                break;
            case DISPLAY_NAME:
                callback.accept(ValueWrapper.handled(player.getDisplayName()));
                break;
            case XP:
                callback.accept(ValueWrapper.handled(String.valueOf(player.getTotalExperience())));
                break;
            case LEVEL:
                callback.accept(ValueWrapper.handled(String.valueOf(player.getLevel())));
                break;
            default:
                callback.accept(ValueWrapper.notHandled());
                break;
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
