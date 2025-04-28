package com.example.proxy.cache;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LRUCache {
    private final Map<String, String> requestToResponseMap = new HashMap<>();
    private final Queue<String> accessOrderQueue = new ArrayDeque<>();
    private final Logger LOG = LoggerFactory.getLogger(LRUCache.class);

    private int maxSize;

    public LRUCache(int maxSize) {
        this.maxSize = maxSize;
    }

    public synchronized String getEntry(String request) {
        if (this.hasEntry(request)) {
            moveToFrontOfQueue(request);
            return requestToResponseMap.get(request);
        }

        return null;
    }

    public synchronized void addEntry(String request, String response) {
        if(requestToResponseMap.size() == maxSize) {
            System.out.println("Max size reached.");
            evictLRUEntry();
        }

        requestToResponseMap.put(request, response);
        LOG.info("Added request {}", request);

        if(accessOrderQueue.contains(request)) {
            moveToFrontOfQueue(request);
        } else {
            accessOrderQueue.add(request);
        }
    }

    public synchronized boolean hasEntry(String request) {
        // TODO: add input validation
        if (requestToResponseMap.containsKey(request)) {
            return true;
        }
        return false;
    }

    public synchronized void clear() {
        requestToResponseMap.clear();
        accessOrderQueue.clear();
    }

    private void evictLRUEntry() {
        String itemToRemove = accessOrderQueue.poll();
        if (requestToResponseMap.remove(itemToRemove) == null) {
            LOG.error("Failed to evict an entry: {}", itemToRemove);
        } else {
            LOG.info("Evicted an entry: {}", itemToRemove);
        }
    }

    public int getCurrentSize() {
        return requestToResponseMap.size();
    }

    private void moveToFrontOfQueue(String request) {
        accessOrderQueue.remove(request);
        accessOrderQueue.add(request);
    }

    /**
     * Temp methods
     */

    public int getQueueSize() {
        return accessOrderQueue.size();
    }

    public int getMapSize() {
        return requestToResponseMap.size();
    }

    public String pollQueue() {
        return accessOrderQueue.poll();
    }
    public synchronized void printStatistics(String request) {
        System.out.println("Entry: " + request + ", " + requestToResponseMap.get(request));
    }

    public synchronized void printStatistics() {
        for (String str : accessOrderQueue) {
            System.out.println("Entry: " + str + ", " + requestToResponseMap.get(str));
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
