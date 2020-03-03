package com.facilio.fw.cache;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class LRUCacheLinkedHashMap<K,V> extends LinkedHashMap<K,V> {

    private float threshold;

    public LRUCacheLinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
        threshold = initialCapacity*loadFactor;
    }

    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > threshold;
    }

    @Override
    public void clear() {
        synchronized (this) {
            super.clear();
        }
    }

    @Override
    public V put(K key, V value) {
        synchronized (this) {
            return super.put(key, value);
        }
    }

    @Override
    public V remove(Object key) {
        synchronized (this) {
            return super.remove(key);
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        synchronized (this) {
            super.putAll(m);
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        synchronized (this) {
            return super.putIfAbsent(key, value);
        }
    }

    public Set<K> cloneKeys() {
        synchronized (this) {
            return new HashSet<>(super.keySet());
        }
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        synchronized (this) {
            return super.computeIfAbsent(key, mappingFunction);
        }
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        synchronized (this) {
            return super.compute(key, remappingFunction);
        }
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        synchronized (this) {
            return super.merge(key, value, remappingFunction);
        }
    }
}
