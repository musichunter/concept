package com.github.linyuzai.event.core.context;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

@Getter
@AllArgsConstructor
public class MapEventContext implements EventContext {

    private final Map<Object, Object> map;

    private final Supplier<Map<Object, Object>> supplier;

    public MapEventContext() {
        this(LinkedHashMap::new);
    }

    public MapEventContext(Supplier<Map<Object, Object>> supplier) {
        this.supplier = supplier;
        this.map = supplier.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> V get(Object key) {
        return (V) map.get(key);
    }

    @Override
    public void put(Object key, Object value) {
        map.put(key, value);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public EventContext duplicate() {
        MapEventContext duplicate = new MapEventContext(supplier);
        duplicate.map.putAll(this.map);
        return duplicate;
    }
}
