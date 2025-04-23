package com.example.proxy.handlers;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.NetSocket;

public class HttpsRequestHandler extends RequestHandler {
    private static final int HTTPS_PORT = 443;
    private final Vertx vertx;

    public HttpsRequestHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    public void handleRequest(HttpServerRequest req, String[] hostAndPort) {
        String host = hostAndPort[0];
        int port = hostAndPort.length == 2 ? Integer.parseInt(hostAndPort[1]) : HTTPS_PORT;
        try {
            vertx.createNetClient().connect(port, host, connectionResult -> {
                if (connectionResult.succeeded()) {
                    NetSocket serverSocket = connectionResult.result();

                    req.toNetSocket()
                            .onSuccess(clientSocket -> {
                                clientSocket.pipeTo(serverSocket)
                                        .onSuccess(v -> {
                                            System.out.println("clientSocket reads are now serverSocket writes!");
                                        })
                                        .onFailure(err -> {
                                            System.out.println("Error: clientSocket reads failed to pipe to serverSocket writes");
                                            err.printStackTrace();
                                        });
                                serverSocket.pipeTo(clientSocket)
                                        .onSuccess(v -> {
                                            System.out.println("serverSocket reads are now clientSocket writes!");
                                        })
                                        .onFailure(err -> {
                                            System.out.println("Error: serverSocket reads failed to pipe to clientSocket writes");
                                            err.printStackTrace();
                                        });
                            })
                            .onFailure(err -> {
                                err.printStackTrace();
                                req.response().setStatusCode(502).end("Error: Failed to downgrade HttpServerRequest to a raw TCP channel. ");
                            });
                } else {
                    System.out.println("Error: Failed to connect to the server at " + host + ":" + port);
                }
            });
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}