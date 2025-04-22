package com.example.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;

public class AsyncExample extends AbstractVerticle {

    @Override
    public void start() {
        HttpClient client = vertx.createHttpClient();
        System.out.println("Running on thread: " + Thread.currentThread().getName());

        System.out.println("Sending async HTTP request to example.com...");

        client.request(HttpMethod.GET, 80, "example.com","/")
                .compose(req -> req.send())
                .onSuccess(response -> {
                    System.out.println("Got response! Status: " + response.statusCode());
                    response.body().onSuccess(body -> {
                        System.out.println("Body length = " + body.length());
                        System.out.println("Body (first 100 chars): \n" + body.toString().substring(0, 100));
                    });
                })
                .onFailure(err -> {
                    System.out.println("Request failed: " + err.getMessage());
                });

        System.out.println("This runs immediately after starting the request!");
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new AsyncExample());
    }
}
