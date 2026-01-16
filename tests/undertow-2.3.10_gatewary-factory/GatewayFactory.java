package org.example;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.handlers.SetHeaderHandler;
import io.undertow.server.handlers.proxy.LoadBalancingProxyClient;
import io.undertow.server.handlers.proxy.ProxyHandler;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class GatewayFactory {

    public static HttpHandler create() {
        try {
            return GatewayBuilder.newBuilder()
                    .addBackend("http://inventory-service:8080")
                    .addBackend("http://inventory-backup:8080")
                    .setConnectionTtl(30, TimeUnit.SECONDS)
                    .setMaxQueueSize(100)
                    .setMaxRetries(3)
                    .withCorsAllowedOrigins("https://myapp.com", "https://admin.myapp.com")
                    .withRateLimit(500)
                    .addResponseHeader("X-Gateway-ID", "Undertow-Edge-1")
                    .mapRoute("/api/v1", "/legacy/api")
                    .mapRoute("/api/v2", "/new/api")
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid Backend URI configuration", e);
        }
    }

    public static class GatewayBuilder {

        private final LoadBalancingProxyClient proxyClient;
        private final PathHandler routingHandler; // Kept as PathHandler

        private int maxRetries = 1;
        private int rateLimit = -1;
        private String[] corsOrigins = new String[0];

        private GatewayBuilder() {
            this.proxyClient = new LoadBalancingProxyClient();

            this.routingHandler = new PathHandler(ResponseCodeHandler.HANDLE_404);
        }

        public static GatewayBuilder newBuilder() {
            return new GatewayBuilder();
        }

        public GatewayBuilder addBackend(String uri) throws URISyntaxException {
            this.proxyClient.addHost(new URI(uri));
            return this;
        }

        public GatewayBuilder setConnectionTtl(long time, TimeUnit unit) {
            this.proxyClient.setTtl((int) unit.toMillis(time));
            return this;
        }

        public GatewayBuilder setMaxQueueSize(int size) {
            this.proxyClient.setMaxQueueSize(size);
            return this;
        }

        public GatewayBuilder setMaxRetries(int retries) {
            this.maxRetries = retries;
            return this;
        }

        public GatewayBuilder withCorsAllowedOrigins(String... origins) {
            this.corsOrigins = origins;
            return this;
        }

        public GatewayBuilder withRateLimit(int requestsPerMinute) {
            this.rateLimit = requestsPerMinute;
            return this;
        }

        public GatewayBuilder addResponseHeader(String header, String value) {
            return this;
        }

        public GatewayBuilder mapRoute(String publicPath, String internalPrefix) {
            HttpHandler proxyHandler = ProxyHandler.builder()
                    .setProxyClient(proxyClient)
                    .setMaxRequestTime(30000)
                    .setRewriteHostHeader(true)
                    .setReuseXForwarded(true)
                    .build();

            HttpHandler pathRewriteHandler = exchange -> {
                String reqPath = exchange.getRelativePath();
                if (reqPath.startsWith(publicPath)) {
                    String newPath = internalPrefix + reqPath.substring(publicPath.length());
                    exchange.setRelativePath(newPath);
                }
                proxyHandler.handleRequest(exchange);
            };

            this.routingHandler.addPrefixPath(publicPath, pathRewriteHandler);
            return this;
        }

        public HttpHandler build() {
            HttpHandler handler = this.routingHandler;

            if (corsOrigins.length > 0) {
                handler = new CorsHandler(handler, corsOrigins);
            }

            if (rateLimit > 0) {
                handler = new SimpleRateLimiter(handler, rateLimit);
            }

            handler = new SetHeaderHandler(handler, "X-Powered-By", "Undertow-Gateway");

            return handler;
        }
    }

    static class CorsHandler implements HttpHandler {
        private final HttpHandler next;
        private final String[] allowedOrigins;

        private static final HttpString ACCESS_CONTROL_ALLOW_ORIGIN =
                new HttpString("Access-Control-Allow-Origin");

        private static final HttpString ACCESS_CONTROL_ALLOW_METHODS =
                new HttpString("Access-Control-Allow-Methods");

        public CorsHandler(HttpHandler next, String[] allowedOrigins) {
            this.next = next;
            this.allowedOrigins = allowedOrigins;
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            String origin = exchange.getRequestHeaders().getFirst(Headers.ORIGIN);
            if (origin != null) {
                for (String allowed : allowedOrigins) {
                    if (allowed.equals(origin)) {
                        exchange.getResponseHeaders().put(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                        exchange.getResponseHeaders().put(ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE");
                        break;
                    }
                }
            }
            next.handleRequest(exchange);
        }
    }

    static class SimpleRateLimiter implements HttpHandler {
        private final HttpHandler next;
        private final int limit;
        private static int requestCount = 0;

        public SimpleRateLimiter(HttpHandler next, int limit) {
            this.next = next;
            this.limit = limit;
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            if (requestCount++ > limit) {
                exchange.setStatusCode(StatusCodes.TOO_MANY_REQUESTS);
                exchange.getResponseSender().send("Rate limit exceeded");
                return;
            }
            next.handleRequest(exchange);
        }
    }
}