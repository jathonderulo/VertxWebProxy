package com.example.proxy.cache;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {
    private final Map<String, String> requestToResponseMap = new HashMap<>();

    public LRUCache() {

    }

    public synchronized void addEntry(String request, String response) {

    }

    public synchronized boolean hasRequest(String request) {
        // TODO: add input validation
        if (requestToResponseMap.containsKey(request)) {
            return true;
        }

        return false;
    }

    public synchronized void clear() {
        requestToResponseMap.clear();
    }

    public synchronized void printStatistics(String request) {
        // TODO: Print all the statistics
    }
}

/**
 * cache.addEntry(request, response)
 * cache.hasRequest(request)
 * cache.clear()
 * cache.printStatistics(request)
 *
 */
