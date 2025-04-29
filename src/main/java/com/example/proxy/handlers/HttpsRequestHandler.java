package com.example.proxy.handlers;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpsRequestHandler implements RequestHandler {
    private static final int HTTPS_PORT = 443;
    private final Vertx vertx;
    private static final Logger LOG = LoggerFactory.getLogger(HttpsRequestHandler.class);

    public HttpsRequestHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    public void handleRequest(HttpServerRequest req, String host, String uri) {
        long startTime = System.currentTimeMillis();
        try {
            vertx.createNetClient().connect(HTTPS_PORT, host, connectionResult -> {
                if (connectionResult.succeeded()) {
                    NetSocket serverSocket = connectionResult.result();

                    req.toNetSocket()
                            .onSuccess(clientSocket -> {
                                clientSocket.pipeTo(serverSocket)
                                        .onFailure(err -> {
                                            err.printStackTrace();
                                        });
                                serverSocket.pipeTo(clientSocket)
                                        .onSuccess(v -> {
                                            long endTime = System.currentTimeMillis();
                                            double duration = ((double) (endTime - startTime));
                                            LOG.info("FULL PROXY: Took {} ms for {}", duration, uri);
                                        })
                                        .onFailure(err -> {
                                            err.printStackTrace();
                                        });
                            })
                            .onFailure(err -> {
                                err.printStackTrace();
                                req.response().setStatusCode(502).end("Error: Failed to downgrade HttpServerRequest to a raw TCP channel. ");
                            });
                } else {
                    System.out.println("Error: Failed to connect to the server at " + host + ":" + HTTPS_PORT);
                }
            });
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}