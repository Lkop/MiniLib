package org.example;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;

public class WebSocketChatHandler {

    public static HttpHandler create() {
        WebSocketConnectionCallback callback = new WebSocketConnectionCallback() {
            @Override
            public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
                System.out.println("Client connected: " + channel.getPeerAddress());

                channel.getReceiveSetter().set(new AbstractReceiveListener() {
                    @Override
                    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                        String incomingText = message.getData();
                        System.out.println("Received: " + incomingText);

                        for (WebSocketChannel peer : channel.getPeerConnections()) {
                            // Don't echo the message back to the sender
                            if (peer != channel) {
                                // "sendText" is non-blocking (async)
                                WebSockets.sendText(incomingText, peer, null);
                            }
                        }
                    }
                });
                channel.resumeReceives();
            }
        };
        return Handlers.websocket(callback);
    }
}
