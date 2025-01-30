package me.hsgamer.cachy.config.converter;

import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.annotation.converter.Converter;

public class StringObjectMapConverter implements Converter {
    @Override
    public Object convert(Object raw) {
        return MapUtils.castOptionalStringObjectMap(raw).orElse(null);
    }

    @Override
    public Object convertToRaw(Object value) {
        return value;
    }
}
