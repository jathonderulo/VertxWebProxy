package com.example.proxy.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LRUCache<K, V> implements Cache<K, V> {
    private final Map<K, V> requestToResponseMap = new HashMap<>();
    private final Queue<K> accessOrderQueue = new ArrayDeque<>();
    private final Logger LOG = LoggerFactory.getLogger(LRUCache.class);

    private final int maxSize;

    public LRUCache(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public synchronized V getEntry(K request) {
        if (this.hasEntry(request)) {
            moveToFrontOfQueue(request);
            LOG.info("Reading {} from cache. ", request);
            return requestToResponseMap.get(request);
        }

        return null;
    }

    @Override
    public synchronized void addEntry(K request, V response) {
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

    @Override
    public synchronized boolean hasEntry(K request) {
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
        K itemToRemove = accessOrderQueue.poll();
        if (requestToResponseMap.remove(itemToRemove) == null) {
            LOG.error("Failed to evict an entry: {}", itemToRemove);
        } else {
            LOG.info("Evicted an entry: {}", itemToRemove);
        }
    }

    private void moveToFrontOfQueue(K request) {
        accessOrderQueue.remove(request);
        accessOrderQueue.add(request);
    }

    public String getCacheContents() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<K, V> entry : requestToResponseMap.entrySet()) {
            sb.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
        }

        return sb.toString();
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

    public K pollQueue() {
        return accessOrderQueue.poll();
    }
}

/**
 * cache.addEntry(request, response)
 * cache.hasRequest(request)
 * cache.clear()
 * cache.printStatistics(request)
 * cache.getEntry(request)
 */
