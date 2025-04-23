package com.example.proxy;

import com.example.proxy.handlers.HttpRequestHandler;
import com.example.proxy.handlers.HttpsRequestHandler;
import com.example.proxy.handlers.RequestHandler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;

import java.util.Arrays;

public class Proxy {
    private final RequestHandler httpRequestHandler;
    private final RequestHandler httpsRequestHandler;
    private final Vertx vertx;
    private final int proxyPort;
    public Proxy(Vertx vertx, int proxyPort) {
        this.vertx = vertx;
        this.proxyPort = proxyPort;
        httpRequestHandler = new HttpRequestHandler(vertx);
        httpsRequestHandler = new HttpsRequestHandler(vertx);
    }

    public void initializeHttpServer() {
        HttpServer server = vertx.createHttpServer();

        server.requestHandler(this::handleRequest);
        server.listen(proxyPort, http -> {
            if (http.succeeded()) {
                System.out.println("HTTP Server start up success!");
            } else {
                System.out.println("Error: HTTP Server start up failed.");
            }
        });
    }

    private void handleRequest(HttpServerRequest req) {
        String[] hostAndPort = req.uri().split(":");
        System.out.println(req.uri());
        if (hostAndPort.length == 0 || hostAndPort.length > 2) {
            System.out.println("Error: hostAndPort is not valid. It was: " + Arrays.toString(hostAndPort));
        }
        if (req.method() == HttpMethod.CONNECT) {
            httpsRequestHandler.handleRequest(req, hostAndPort);
        } else {
            httpRequestHandler.handleRequest(req, hostAndPort);
        }
    }
}
