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
}
