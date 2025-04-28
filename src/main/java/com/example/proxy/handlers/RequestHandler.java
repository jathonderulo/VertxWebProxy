package com.example.proxy.handlers;

import io.vertx.core.http.HttpServerRequest;

import java.net.URISyntaxException;

public interface RequestHandler {
    void handleRequest(HttpServerRequest req, String domain) throws URISyntaxException;
}
