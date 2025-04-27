package com.example.proxy.cache;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {
    private final Map<String, String> requestToResponseMap = new HashMap<>();

    public LRUCache() {

    }
}
