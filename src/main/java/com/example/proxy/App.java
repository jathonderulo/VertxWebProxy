package com.example.proxy;

import io.github.cdimascio.dotenv.Dotenv;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.core.net.NetSocket;

import java.util.List;
import java.util.Map;


/**
 * Hello world!
 *p
 */
public class App extends AbstractVerticle
{
    private static final Dotenv dotenv = Dotenv.load();

    public static void main( String[] args )
    {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new App());
    }

    /**
     * Overridden method from AbstractVerticle. Called when the Verticle is deployed to initialize start logic.
     * @throws Exception
     */
    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer();
        int listenOnPort = Integer.parseInt(dotenv.get("PORT_TO_SIT_ON"));

        // Assign the request handler to the server, requests will go there
        server.requestHandler(this::handleProxyRequest);

        // Make the server listen to a specific port and return result of http
        // basically, the listen function runs an async op which will return a HttpServer async result
        server.listen(listenOnPort, http -> {
            if (http.succeeded()) {
                System.out.println("Proxy server listening on http://localhost:" + listenOnPort);
            } else {
                System.err.println("Failed to start proxy server: " + http.cause());
            }
        });
    }

    private void handleProxyRequest(HttpServerRequest req) {
        if(req.method() == HttpMethod.CONNECT) {
            handleProxyRequestHTTPS(req);
        } else {
            handleProxyRequestHTTP(req);
        }
    }

    private void handleProxyRequestHTTPS(HttpServerRequest req) {
        // Extract target host and port from the request URI
        System.out.println("HTTPS URI: " + req.uri());
        String[] splitUri = req.uri().split(":"); // example.com:3000

        if (splitUri.length < 2) {
            req.response().setStatusCode(400).end("Invalid CONNECT request format");
            return;
        }

        String targetHost = splitUri[0];
        int targetPort = Integer.parseInt(splitUri[1]);

        // Create a NetClient to establish a TCP connection to the target server
        vertx.createNetClient().connect(targetPort, targetHost, connectionResult -> {
            if(connectionResult.succeeded()) {
                NetSocket targetSocket = connectionResult.result();

                // Pretty much strip away the HTTP part of the TCP connection that is 'req' and just use it a TCP channel
                req.toNetSocket()
                        // On success, let the target know and then set up the piping
                        .onSuccess(clientSocket -> {
                                // Literally whatever comes into clientSocket, send to targetSocket. Async op tho
                                clientSocket.pipeTo(targetSocket)
                                        .onSuccess(v2 -> {
                                            System.out.println("Client to Target pipe established!");
                                        })
                                        .onFailure(err -> {
                                            System.out.println("Client to Target pipe failed...");
                                            err.printStackTrace();
                                        });
                                // Literally whatever comes into targetSocket, send to clientSocket. Async op tho
                                targetSocket.pipeTo(clientSocket)
                                        .onSuccess(v3 -> {
                                            System.out.println("Target to client pipe established!");
                                        })
                                        .onFailure(err -> {
                                            System.out.println("Target to client pipe failed...");
                                            err.printStackTrace();
                                        });
                        })
                        // On failure, let the client know
                        .onFailure(err -> {
                            req.response().setStatusCode(500).end("Failed to establish tunnel");
                            targetSocket.close();
                        });
            } else {
                req.response().setStatusCode(502).end("The connection to the target server didn't succeed");
            }
        });
    }

    /**
     * Handling requests. Currently just HTTP.
     * @param req
     */
    private void handleProxyRequestHTTP(HttpServerRequest req) {
        // Create HTTP client to represent a client to be able to send the request to the actual server
        HttpClient client = vertx.createHttpClient();

        // Some parsing and printing
        int targetPort = Integer.parseInt(dotenv.get("HTTP_PORT"));
        MultiMap map = req.headers();
        String targetHost = map.get("Host");

        // The incoming request's body handler
        req.bodyHandler(body -> {
            /// Copying the incoming request received over to the client to form an identical outgoing request
            // Create a request with the method, port, host, and resource
            client.request(req.method(), targetPort, targetHost, req.uri())
                    // Actually send the request, return a future<response>
                    .compose(clientRequest -> {
                        clientRequest.headers().setAll(req.headers());
                        return clientRequest.send(body);
                    })
                    // On success, print success and populate the response object of the laptop's request
                    // This clientResponse object represents the response metadata, the body might still be streamed over
                    // because http is chunked and streamed
                    .onSuccess(clientResponse -> {
                        System.out.println("Successfully got a response");
                        req.response().setStatusCode(clientResponse.statusCode());
                        req.response().headers().setAll(clientResponse.headers());

                        // Get the response body, this is an async op because the body might not be fully transmitted
                        // even if clientResponse is ready
                        clientResponse.body()
                                // On success, call req.response().end() with the bodyBuffer, which forwards the
                                // response to the original client
                                .onSuccess(bodyBuffer -> req.response().end(bodyBuffer))

                                // On failure, print the stack trace of the error and send back HTTP error
                                .onFailure(err -> {
                                    err.printStackTrace();
                                    req.response().setStatusCode(502).end("Failed to read the body of the response from the server.");
                                });
                    })
                    // On failure, print err stack trace and send back HTTP error
                    .onFailure(err -> {
                        System.out.println("Did not get a response.");
                        err.printStackTrace();
                        req.response().setStatusCode(502).end("Proxy: Request to the server failed\n");
                    });
        });
    }
}
