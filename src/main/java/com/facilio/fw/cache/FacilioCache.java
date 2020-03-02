package com.facilio.fw.cache;

import java.util.Set;

public interface FacilioCache<K, V> {

    boolean contains(K key);
    V get(K key);
    void put(K key, V value);
    void remove(K key);
    void purgeCache();
    Set<K> keys();
}
