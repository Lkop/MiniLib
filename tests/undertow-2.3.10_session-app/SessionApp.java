package org.example;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionAttachmentHandler;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionCookieConfig;
import io.undertow.server.session.SessionListener;
import io.undertow.server.session.SessionManager;
import io.undertow.util.Headers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionApp {

    public static void init() {
        SessionInfrastructure infra = new SessionInfrastructure();

        HttpHandler myLogic = exchange -> {
            Session session = infra.getOrCreateSession(exchange);

            Integer visits = (Integer) session.getAttribute("visits");
            if (visits == null) {
                visits = 0;
            }

            visits++;
            session.setAttribute("visits", visits);

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send(
                    "Your Session ID: " + session.getId() + "\n" +
                            "You have visited this page " + visits + " times.\n" +
                            "Total active users on server: " + infra.getCurrentActiveUserCount()
            );
        };

        HttpHandler rootHandler = infra.getAttachmentHandler(myLogic);

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(rootHandler)
                .build();
    }

    static class SessionInfrastructure {

        private final SessionManager sessionManager;
        private final SessionConfig sessionConfig;
        private final ActiveUserTracker tracker;

        public SessionInfrastructure() {
            this.tracker = new ActiveUserTracker();

            InMemorySessionManager manager = new InMemorySessionManager("MY_SESSION_ID", 1000, true);
            manager.setDefaultSessionTimeout(30 * 60);
            manager.registerSessionListener(tracker);

            this.sessionManager = manager;

            SessionCookieConfig cookieConfig = new SessionCookieConfig();
            cookieConfig.setCookieName("JSESSIONID");
            cookieConfig.setHttpOnly(true);
            cookieConfig.setPath("/");

            this.sessionConfig = cookieConfig;
        }

        public Session getOrCreateSession(HttpServerExchange exchange) {
            Session session = sessionManager.getSession(exchange, sessionConfig);
            if (session == null) {
                session = sessionManager.createSession(exchange, sessionConfig);
            }
            return session;
        }

        public SessionAttachmentHandler getAttachmentHandler(HttpHandler next) {
            return new SessionAttachmentHandler(next, sessionManager, sessionConfig);
        }

        public int getCurrentActiveUserCount() {
            return tracker.getActiveCount();
        }
        
        private static class ActiveUserTracker implements SessionListener {
            private final Map<String, Long> activeSessions = new ConcurrentHashMap<>();

            public int getActiveCount() { return activeSessions.size(); }

            @Override
            public void sessionCreated(Session session, HttpServerExchange exchange) {
                activeSessions.put(session.getId(), System.currentTimeMillis());
                System.out.println(">> [Session] Created: " + session.getId());
            }

            @Override
            public void sessionDestroyed(Session session, HttpServerExchange exchange, SessionDestroyedReason reason) {
                activeSessions.remove(session.getId());
                System.out.println("<< [Session] Destroyed: " + session.getId());
            }

            @Override public void attributeAdded(Session s, String n, Object v) {}
            @Override public void attributeUpdated(Session s, String n, Object v, Object o) {}
            @Override public void attributeRemoved(Session s, String n, Object o) {}
            @Override public void sessionIdChanged(Session s, String o) {}
        }
    }
}