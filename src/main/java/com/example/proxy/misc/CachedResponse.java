package com.example.proxy.misc;

import io.vertx.core.MultiMap;

public class CachedResponse {
    public final int statusCode;
    public final String body;
    public final MultiMap headers;

    public CachedResponse(int statusCode, String body, MultiMap headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }
}
