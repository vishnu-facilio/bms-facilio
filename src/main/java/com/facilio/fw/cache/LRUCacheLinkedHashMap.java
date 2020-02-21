package com.facilio.fw.cache;

import java.util.LinkedHashMap;
import java.util.Map;

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
}
