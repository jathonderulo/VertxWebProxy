package com.example.proxy.cache;

public interface Cache<K, V> {
    V getEntry(K key);
    void addEntry(K key, V value);
    boolean hasEntry(K key);
    void clear();
}
