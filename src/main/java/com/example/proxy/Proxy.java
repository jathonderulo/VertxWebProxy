package com.example.proxy;

import com.example.proxy.handlers.HttpRequestHandler;
import com.example.proxy.handlers.HttpsRequestHandler;
import com.example.proxy.handlers.RequestHandler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;

import java.net.URI;
import java.net.URISyntaxException;

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

    private void handleRequest(HttpServerRequest req)  {

        try {
            URI uri = new URI(req.uri());
            String domain = uri.getHost();
            System.out.println(req.uri());
            if (req.method() == HttpMethod.CONNECT) {
                httpsRequestHandler.handleRequest(req, domain);
            } else {
                httpRequestHandler.handleRequest(req, domain);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
