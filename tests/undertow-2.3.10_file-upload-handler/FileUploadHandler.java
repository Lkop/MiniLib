package org.example;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.Headers;

public class FileUploadHandler {

    public static HttpHandler create() {
        HttpHandler logic = new HttpHandler() {
            @Override
            public void handleRequest(HttpServerExchange exchange) throws Exception {
                String contentType = exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
                if (contentType == null || !contentType.startsWith("multipart/form-data")) {
                    exchange.setStatusCode(400);
                    exchange.getResponseSender().send("Content-Type must be multipart/form-data");
                    return;
                }

                FormDataParser parser = FormParserFactory.builder().build().createParser(exchange);
                FormData formData = parser.parseBlocking();
                FormData.FormValue fileValue = formData.getFirst("myFile");

                if (fileValue != null && fileValue.isFileItem()) {
                    String fileName = fileValue.getFileName();
                    long size = fileValue.getFileItem().getFileSize();

                    exchange.getResponseSender().send("Success! Received: " + fileName + " (" + size + " bytes)");
                } else {
                    exchange.setStatusCode(400);
                    exchange.getResponseSender().send("No file found in form field 'myFile'");
                }
                parser.close();
            }
        };
        return new BlockingHandler(logic);
    }
}