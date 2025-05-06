package com.example.proxy.misc;

import io.vertx.core.MultiMap;

public class CachedResponse {
    public final String body;
    public final MultiMap headers;

    public CachedResponse(String body, MultiMap headers) {
        this.body = body;
        this.headers = headers;
    }
}
