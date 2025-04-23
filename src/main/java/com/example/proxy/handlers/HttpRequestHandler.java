package com.example.proxy.handlers;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServerRequest;

public class HttpRequestHandler implements RequestHandler {
    private static final int HTTP_PORT = 80;
    private final Vertx vertx;
    private final HttpClient client;

    public HttpRequestHandler(Vertx vertx) {
        this.vertx = vertx;
        this.client = vertx.createHttpClient();
    }

    public void handleRequest(HttpServerRequest req, String[] hostAndPort) {
        String host = hostAndPort[0];
        int port = hostAndPort.length == 2 ? Integer.parseInt(hostAndPort[1]) : HTTP_PORT;

        req.bodyHandler(requestBody -> {
            client.request(req.method(), port, host, req.uri())
                    .compose(clientRequest -> {
                        clientRequest.headers().setAll(req.headers());
                        return clientRequest.send(requestBody);
                    })
                    .onSuccess(clientResponse -> {
                        req.response().setStatusCode(clientResponse.statusCode());
                        req.response().headers().setAll(clientResponse.headers());

                        clientResponse.body()
                                .onSuccess(responseBody -> {
                                    req.response().end(responseBody);
                                })
                                .onFailure(err -> {
                                    err.printStackTrace();
                                    req.response().setStatusCode(502).end("Request back to client failed...");
                                });
                    })
                    .onFailure(err -> {
                        err.printStackTrace();
                        req.response().setStatusCode(502).end("Request to server failed");
                    });
        });
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }
}