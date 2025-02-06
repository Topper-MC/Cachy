package me.hsgamer.cachy.config.converter;

import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.annotation.converter.Converter;

import java.util.AbstractMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringObjectValueMapConverter implements Converter {
    @Override
    public Object convert(Object raw) {
        return MapUtils.castOptionalStringObjectMap(raw)
                .map(map -> map.entrySet()
                        .stream()
                        .flatMap(entry -> MapUtils.castOptionalStringObjectMap(entry.getValue())
                                .map(valueMap -> new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), valueMap))
                                .map(Stream::of)
                                .orElseGet(Stream::empty)
                        )
                        .collect(Collectors.toMap(AbstractMap.SimpleImmutableEntry::getKey, AbstractMap.SimpleImmutableEntry::getValue, (a, b) -> b))
                ).orElse(null);
    }

    @Override
    public Object convertToRaw(Object value) {
        return value;
    }
}
