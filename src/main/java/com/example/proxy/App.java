package com.example.proxy;

import com.example.proxy.handlers.HttpRequestHandler;
import com.example.proxy.handlers.HttpsRequestHandler;
import com.example.proxy.handlers.RequestHandler;
import com.example.proxy.webprotocols.WebProtocol;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

import java.util.HashMap;
import java.util.Map;


public class App extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new App());
    }

    @Override
    public void start() {
        int port = 4000;
        Map<WebProtocol, RequestHandler> handlers = new HashMap<>();
        handlers.put(WebProtocol.Http, new HttpRequestHandler(vertx));
        handlers.put(WebProtocol.Https, new HttpsRequestHandler(vertx));

        Proxy proxy = new Proxy(vertx, port, handlers);
        proxy.initializeHttpServer();
    }
}
