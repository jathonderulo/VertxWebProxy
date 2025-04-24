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

    public void handleRequest(HttpServerRequest req, String[] hostAndPort) {
        long startTime = System.currentTimeMillis();
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
//                                            System.out.println("clientSocket reads sent to serverSocket writes!");
                                        })
                                        .onFailure(err -> {
//                                            System.out.println("Error: clientSocket reads failed to pipe to serverSocket writes");
                                            err.printStackTrace();
                                        });
                                serverSocket.pipeTo(clientSocket)
                                        .onSuccess(v -> {
//                                            System.out.println("serverSocket reads sent to clientSocket writes!");
                                            long endTime = System.currentTimeMillis();
                                            double duration = ((double) (endTime - startTime));
                                            LOG.info("FULL PROXY: Took {} ms for {}", duration, req.uri());
                                        })
                                        .onFailure(err -> {
//                                            System.out.println("Error: serverSocket reads failed to pipe to clientSocket writes");
                                            err.printStackTrace();
                                        });
                            })
                            .onFailure(err -> {
                                err.printStackTrace();
                                req.response().setStatusCode(502).end("Error: Failed to downgrade HttpServerRequest to a raw TCP channel. ");
                            });
                } else {
//                    System.out.println("Error: Failed to connect to the server at " + host + ":" + port);
                }
            });
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}