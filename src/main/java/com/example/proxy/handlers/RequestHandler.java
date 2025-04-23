package com.example.proxy.handlers;

import io.vertx.core.http.HttpServerRequest;

public abstract class RequestHandler {
    public abstract void handleRequest(HttpServerRequest req, String[] hostAndPort);
}
