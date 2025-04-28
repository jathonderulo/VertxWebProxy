package com.example.proxy.handlers;

import io.vertx.core.http.HttpServerRequest;

public interface RequestHandler {
    void handleRequest(HttpServerRequest req, String domain);
}
