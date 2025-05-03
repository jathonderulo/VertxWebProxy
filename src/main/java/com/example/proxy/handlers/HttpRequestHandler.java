package com.example.proxy.handlers;

import com.example.proxy.cache.LRUCache;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestHandler implements RequestHandler {
    private static final int HTTP_PORT = 80;
    private final HttpClient client;
    private final LRUCache<String, String> cache = new LRUCache<>(5);
    Logger LOG = LoggerFactory.getLogger(HttpRequestHandler.class);

    public HttpRequestHandler(Vertx vertx) {
        this.client = vertx.createHttpClient();
    }

    public void handleRequest(HttpServerRequest req, String host, String uri) {
        if (cache.hasEntry(uri)) {
            LOG.info("Reading {} from cache!", uri);
            req.response().setStatusCode(200).end(cache.getEntry(uri));
            return;
        }

        req.bodyHandler(requestBody -> {
            client.request(req.method(), HTTP_PORT, host, uri)
                    .compose(clientRequest -> {
                        clientRequest.headers().setAll(req.headers());
                        return clientRequest.send(requestBody);
                    })
                    .onSuccess(clientResponse -> {
                        req.response().setStatusCode(clientResponse.statusCode());
                        req.response().headers().setAll(clientResponse.headers());

                        clientResponse.body()
                                .onSuccess(responseBody -> {
                                    cache.addEntry(uri, responseBody.toString());
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
}