package com.facilio.collections;

import java.util.HashMap;

public class UniqueMap<K, V> extends HashMap<K, V> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public V put(K key, V value) {
		// TODO Auto-generated method stub
		if(containsKey(key)) {
			throw new IllegalArgumentException("Key "+String.valueOf(key)+" already exists in the map");
		}
		return super.put(key, value);
	}
}
