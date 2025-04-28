package com.example.proxy.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LRUCacheTest {
    private LRUCache cache;

    @BeforeEach
    void setup() {
        cache = new LRUCache(3);
    }

    @Test
    void testAddToCache() {
        cache.addEntry("Request entry", "Response entry");
        assertEquals("Response entry", cache.getEntry("Request entry"));
    }

    @Test
    void testUpdateRecentlyUsed() {
        cache.addEntry("Request entry 1", "Response entry 1");
        cache.addEntry("Request entry 2", "Response entry 2");
        cache.addEntry("Request entry 3", "Response entry 3");

        cache.getEntry("Request entry 1");

        assertEquals("Request entry 2", cache.pollQueue());
        assertEquals("Request entry 3", cache.pollQueue());
        assertEquals("Request entry 1", cache.pollQueue());
    }

    @Test
    void testEvictWhenFull() {
        cache.addEntry("Request entry 1", "Response entry 1");
        cache.addEntry("Request entry 2", "Response entry 2");
        cache.addEntry("Request entry 3", "Response entry 3");
        cache.addEntry("Request entry 4", "Response entry 4");
        cache.addEntry("Request entry 5", "Response entry 5");
        assertEquals(3, cache.getQueueSize());
        assertEquals(3, cache.getMapSize());
    }

    @Test
    void testClearCache() {
        cache.addEntry("Request entry 1", "Response entry 1");
        cache.addEntry("Request entry 2", "Response entry 2");
        cache.addEntry("Request entry 3", "Response entry 3");
        cache.clear();

        assertEquals(0, cache.getMapSize());
        assertEquals(0, cache.getQueueSize());
    }


}
