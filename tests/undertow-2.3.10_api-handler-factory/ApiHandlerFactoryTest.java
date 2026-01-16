package org.example;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

public class ApiHandlerFactory {

    public static HttpHandler createRootHandler() {
        RoutingHandler routes = new RoutingHandler()
                .get("/api/status", exchange -> {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    exchange.getResponseSender().send("{\"status\": \"healthy\"}");
                })
                .get("/api/users/{id}", exchange -> {
                    String userId = exchange.getQueryParameters().get("id").getFirst();

                    if ("0".equals(userId)) {
                        throw new IllegalArgumentException("Invalid User ID: 0");
                    }

                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    exchange.getResponseSender().send("{\"user\": \"User" + userId + "\"}");
                });

        return new GlobalExceptionHandler(routes);
    }
    
    private static class GlobalExceptionHandler implements HttpHandler {

        private final HttpHandler next;

        public GlobalExceptionHandler(HttpHandler next) {
            this.next = next;
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            // We dispatch to the next handler inside a try/catch block
            if (exchange.isInIoThread()) {
                exchange.dispatch(this);
                return;
            }

            try {
                next.handleRequest(exchange);
            } catch (Throwable t) {
                if (!exchange.isResponseStarted()) {
                    exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    exchange.getResponseSender().send("{\"error\": \"" + t.getMessage() + "\"}");
                } else {
                    // If response already started, we can't change the status code
                    t.printStackTrace();
                }
            }
        }
    }
}