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
    void testAddToCache() throws Exception {
        cache.addEntry("Request entry", "Response entry");
        assertEquals("Response entry", cache.getEntry("Request entry"));
    }
}
