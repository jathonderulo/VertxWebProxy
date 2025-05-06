package com.example.proxy;

import com.example.proxy.handlers.HttpRequestHandler;
import com.example.proxy.handlers.HttpsRequestHandler;
import com.example.proxy.handlers.RequestHandler;
import com.example.proxy.misc.WebProtocol;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;

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
        String uri = req.uri();

        if (uri.equals("/printCache")) {
            handlePrintCache(req);
            return;
        }

        String[] uriSplitByColon = uri.split(":");
        String host;
        // some weird URI parsing. sometimes we get example.com:443 and sometimes we get http://example.com
        if (uriSplitByColon[0].equals("http") || uriSplitByColon[0].equals("https")) {
            String urlWithoutLeadingSlashes = uriSplitByColon[1].substring(2); // skip the first two forward slashes
            host = urlWithoutLeadingSlashes.split("/")[0]; // should be only one forward slash left
        } else {
            host = uriSplitByColon[0];
        }
        System.out.println("Host is " + host);

        WebProtocol protocol =
            req.method() == HttpMethod.CONNECT
                ? WebProtocol.Https
                : WebProtocol.Http;
        handlers.get(protocol).handleRequest(req, host, uri);
    }

    private void handlePrintCache(HttpServerRequest req) {
        RequestHandler httpHandler = handlers.get(WebProtocol.Http);
        if (httpHandler != null) {
            String cacheContents = ((HttpRequestHandler) httpHandler).getCacheContents();
            req.response()
                    .putHeader("content-type", "application/json")
                    .end(cacheContents);
        }
    }
}
