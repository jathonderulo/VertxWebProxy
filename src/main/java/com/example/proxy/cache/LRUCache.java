package com.example.proxy.cache;

import java.util.*;

public class LRUCache {
    private final Map<String, String> requestToResponseMap = new HashMap<>();
    private final Queue<String> accessOrderQueue = new ArrayDeque<>();
    private int maxSize;

    public LRUCache(int maxSize) {
        this.maxSize = maxSize;
    }

    public synchronized String getEntry(String request) throws Exception {
        if (this.hasRequest(request)) {
            return requestToResponseMap.get(request);
        } else {
            throw new Exception("Entry not there");
        }
    }

    public synchronized void addEntry(String request, String response) {
        if(requestToResponseMap.size() == maxSize) {
            System.out.println("Max size reached.");
            evictLRUEntry();
        }

        requestToResponseMap.put(request, response);
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

    private void evictLRUEntry() {
        String itemToRemove = accessOrderQueue.poll();
        if (requestToResponseMap.remove(itemToRemove) == null) {
           System.out.println("Failed to evict an entry.");
        } else {
            System.out.println("Evicted an entry.");
        }
    }
}

/**
 * cache.addEntry(request, response)
 * cache.hasRequest(request)
 * cache.clear()
 * cache.printStatistics(request)
 * cache.getEntry(request)
 */
