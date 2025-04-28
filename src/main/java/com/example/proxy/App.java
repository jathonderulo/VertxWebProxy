package com.example.proxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.github.cdimascio.dotenv.Dotenv;


public class App extends AbstractVerticle {
    private static final Dotenv dotenv = Dotenv.load();

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new App());
    }

    @Override
    public void start() {
        Proxy proxy = new Proxy(vertx, Integer.parseInt(dotenv.get("PORT_TO_SIT_ON")));
        proxy.initializeHttpServer();
    }
}
