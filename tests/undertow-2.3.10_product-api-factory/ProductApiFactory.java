package org.example;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ProductApiFactory {

    // Entry point: Returns the fully configured application handler
    public static HttpHandler create() {
        // 1. Wiring Dependencies
        ProductService service = new ProductService();
        ProductController controller = new ProductController(service);

        // 2. Define Routes
        RoutingHandler routes = new RoutingHandler()
                .get("/products", controller::listAll)
                .get("/products/{id}", controller::getOne)
                // POST requires reading the body, so we wrap it in a BlockingHandler
                .post("/products", new BlockingHandler(controller::create))
                .delete("/products/{id}", controller::deleteOne);

        // 3. Wrap in Global Middleware (Error Handling & Common Headers)
        return new GlobalMiddleware(routes);
    }

    // ==========================================
    //       LAYER 1: CONTROLLER (Web Layer)
    // ==========================================
    static class ProductController {
        private final ProductService service;

        public ProductController(ProductService service) {
            this.service = service;
        }

        public void listAll(HttpServerExchange exchange) {
            Collection<Product> products = service.findAll();
            sendJson(exchange, jsonFromList(products));
        }

        public void getOne(HttpServerExchange exchange) {
            String idStr = exchange.getQueryParameters().get("id").getFirst();
            Long id = parseId(idStr);

            Product product = service.findById(id);
            if (product == null) {
                throw new NotFoundException("Product not found: " + id);
            }
            sendJson(exchange, product.toJson());
        }

        public void create(HttpServerExchange exchange) throws IOException {
            // NOTE: This runs in a worker thread because of BlockingHandler
            if (exchange.isInIoThread()) {
                exchange.dispatch(this::create);
                return;
            }

            // Read Request Body
            String body = new String(exchange.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (body.isEmpty()) throw new IllegalArgumentException("Empty body");

            // Very simple parsing (Real apps would use Jackson/Gson)
            String name = extractJsonValue(body, "name");
            double price = Double.parseDouble(extractJsonValue(body, "price"));

            Product created = service.save(name, price);

            exchange.setStatusCode(StatusCodes.CREATED);
            sendJson(exchange, created.toJson());
        }

        public void deleteOne(HttpServerExchange exchange) {
            String idStr = exchange.getQueryParameters().get("id").getFirst();
            Long id = parseId(idStr);

            if (service.delete(id)) {
                exchange.setStatusCode(StatusCodes.NO_CONTENT);
            } else {
                throw new NotFoundException("Product not found for deletion");
            }
        }

        // --- Helpers ---
        private void sendJson(HttpServerExchange exchange, String json) {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(json);
        }

        private Long parseId(String id) {
            try { return Long.parseLong(id); }
            catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid ID format"); }
        }

        // Mock JSON Parser to avoid external dependencies in this example
        private String extractJsonValue(String json, String key) {
            int start = json.indexOf("\"" + key + "\"");
            if (start == -1) throw new IllegalArgumentException("Missing JSON key: " + key);
            int colon = json.indexOf(":", start);
            int valStart = json.indexOf("\"", colon) + 1;
            if (valStart == 0) { // It might be a number (no quotes)
                valStart = colon + 1;
                while(Character.isWhitespace(json.charAt(valStart))) valStart++;
                int valEnd = valStart;
                while(valEnd < json.length() && (Character.isDigit(json.charAt(valEnd)) || json.charAt(valEnd) == '.')) valEnd++;
                return json.substring(valStart, valEnd);
            }
            int valEnd = json.indexOf("\"", valStart);
            return json.substring(valStart, valEnd);
        }

        private String jsonFromList(Collection<Product> list) {
            StringBuilder sb = new StringBuilder("[");
            for (Product p : list) sb.append(p.toJson()).append(",");
            if (list.size() > 0) sb.setLength(sb.length() - 1); // remove last comma
            return sb.append("]").toString();
        }
    }

    // ==========================================
    //       LAYER 2: SERVICE (Business Logic)
    // ==========================================
    static class ProductService {
        private final Map<Long, Product> db = new ConcurrentHashMap<>();
        private final AtomicLong idGenerator = new AtomicLong(1);

        public ProductService() {
            // Seed data
            save("Laptop", 999.99);
            save("Mouse", 25.50);
        }

        public Collection<Product> findAll() { return db.values(); }
        public Product findById(Long id) { return db.get(id); }
        public boolean delete(Long id) { return db.remove(id) != null; }

        public Product save(String name, double price) {
            long id = idGenerator.getAndIncrement();
            Product p = new Product(id, name, price);
            db.put(id, p);
            return p;
        }
    }

    // ==========================================
    //       LAYER 3: DOMAIN (Model)
    // ==========================================
    static class Product {
        final long id;
        final String name;
        final double price;

        Product(long id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        String toJson() {
            return String.format("{\"id\":%d, \"name\":\"%s\", \"price\":%.2f}", id, name, price);
        }
    }

    // ==========================================
    //       LAYER 4: MIDDLEWARE (Exceptions)
    // ==========================================
    static class GlobalMiddleware implements HttpHandler {
        private final HttpHandler next;

        public GlobalMiddleware(HttpHandler next) { this.next = next; }

        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            try {
                next.handleRequest(exchange);
            } catch (Throwable t) {
                if (exchange.isResponseStarted()) {
                    t.printStackTrace(); // Can't change response now
                    return;
                }

                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

                if (t instanceof NotFoundException) {
                    exchange.setStatusCode(StatusCodes.NOT_FOUND);
                    exchange.getResponseSender().send("{\"error\": \"" + t.getMessage() + "\"}");
                } else if (t instanceof IllegalArgumentException) {
                    exchange.setStatusCode(StatusCodes.BAD_REQUEST);
                    exchange.getResponseSender().send("{\"error\": \"Bad Request: " + t.getMessage() + "\"}");
                } else {
                    exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
                    exchange.getResponseSender().send("{\"error\": \"Internal System Error\"}");
                    t.printStackTrace();
                }
            }
        }
    }

    static class NotFoundException extends RuntimeException {
        public NotFoundException(String msg) { super(msg); }
    }
}