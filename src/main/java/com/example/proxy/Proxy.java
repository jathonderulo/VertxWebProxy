package com.example.proxy;

import com.example.proxy.handlers.RequestHandler;
import com.example.proxy.webprotocols.WebProtocol;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class Proxy {
    private final Vertx vertx;
    private final int proxyPort;
    private final Map<WebProtocol, RequestHandler> handlers;

    public Proxy(Vertx vertx, int proxyPort, Map<WebProtocol, RequestHandler> handlers) {
        this.vertx = vertx;
        this.proxyPort = proxyPort;
        this.handlers = handlers;
    }

    public void initializeHttpServer() {
        HttpServer server = vertx.createHttpServer();

        server.requestHandler(this::handleRequest);
        server.listen(proxyPort, result -> {
            if (result.succeeded()) {
                System.out.println("HTTP server start up success! Listening on port " + proxyPort);
            } else {
                System.out.println("Error: HTTP server start up failed. Cause: " + result.cause());
            }
        });
    }

    private void handleRequest(HttpServerRequest req)  {
        try {
            String uri = req.uri();
            String host = new URI(uri).getHost();
            System.out.println(req.uri());

            WebProtocol protocol =
                req.method() == HttpMethod.CONNECT
                    ? WebProtocol.Https
                    : WebProtocol.Http;
            handlers.get(protocol).handleRequest(req, host, uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
