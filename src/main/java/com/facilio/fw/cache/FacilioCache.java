package com.facilio.fw.cache;

public interface FacilioCache<K, V> {

    boolean contains(K key);
    V get(K key);
    void put(K key, V value);
    void remove(K key);
    void purgeCache();
}
