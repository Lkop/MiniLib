import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.util.Headers;


public class NewTest {

    static class HelloHandler implements HttpHandler {
        @Override
        public void handleRequest(HttpServerExchange exchange) {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Hello from Undertow!");
        }
    }

    static class LoggingHandler implements HttpHandler {
        private final HttpHandler next;

        public LoggingHandler(HttpHandler next) {
            this.next = next;
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            System.out.println("Request path: " + exchange.getRequestPath());
            next.handleRequest(exchange);
        }
    }

    static class JsonHandler implements HttpHandler {
        @Override
        public void handleRequest(HttpServerExchange exchange) {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("{\"status\":\"ok\",\"service\":\"undertow\"}");
        }
    }

    public static HttpHandler getRoutes() {
        PathHandler routes = new PathHandler()
                .addExactPath("/hello", new HelloHandler())
                .addExactPath("/json", new JsonHandler());

        return new LoggingHandler(routes);
    }


    public void mainTest(String[] args) {
        HttpHandler handler = getRoutes();
        System.out.println("Handler ready! You can plug it into any Undertow server.");
    }
}
