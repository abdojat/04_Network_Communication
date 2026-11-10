import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/** Original worker server, extended with the PDF's /api/analyze endpoint. */
public class WebServer {
    static final Logger logger = Logger.getLogger("WebServer");
    private static final String TASK_ENDPOINT = "/task";
    private static final String STATUS_ENDPOINT = "/status";
    private static final String ANALYZE_ENDPOINT = "/api/analyze";
    private final int port;
    private HttpServer server;

    public WebServer(int port) { this.port = port; }

    public static void main(String[] args) {
        int serverPort = args.length == 1 ? Integer.parseInt(args[0]) : 8080;
        new WebServer(serverPort).startServer();
        System.out.println("Server is listening on port " + serverPort);
    }

    public void startServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new RuntimeException("Cannot start HTTP server on port " + port, e);
        }
        server.createContext(STATUS_ENDPOINT).setHandler(this::handleStatusCheckRequest);
        server.createContext(TASK_ENDPOINT).setHandler(this::handleTaskRequest);
        server.createContext(ANALYZE_ENDPOINT).setHandler(this::handleAnalyzeRequest);
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
    }

    private void handleTaskRequest(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "POST");
            sendResponse(exchange, 405, "Only POST is allowed\n", "text/plain; charset=UTF-8");
            return;
        }
        Headers headers = exchange.getRequestHeaders();
        if ("true".equalsIgnoreCase(headers.getFirst("X-Test"))) {
            sendResponse(exchange, 200, "123\n", "text/plain; charset=UTF-8");
            return;
        }
        long startTime = System.nanoTime();
        String request = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            String response = calculateResponse(request);
            if ("true".equalsIgnoreCase(headers.getFirst("X-Debug"))) {
                exchange.getResponseHeaders().set("X-Debug-Info", "Operation took " + (System.nanoTime() - startTime) + " ns");
            }
            sendResponse(exchange, 200, response, "text/plain; charset=UTF-8");
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid integer list; use e.g. 10,200\n", "text/plain; charset=UTF-8");
        }
    }

    private String calculateResponse(String request) {
        String[] numbers = request.trim().split(",");
        BigInteger result = BigInteger.ONE;
        for (String number : numbers) {
            result = result.multiply(new BigInteger(number.trim()));
        }
        return "Result of the multiplication is " + result + "\n";
    }

    /** For JSON experiment 1.1: accept the JSON body as a transport example.
     * This is an echo endpoint, intentionally NOT a schema-enforcing parser. */
    private void handleAnalyzeRequest(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "POST");
            sendResponse(exchange, 405, "{\"error\":\"POST required\"}", "application/json; charset=UTF-8");
            return;
        }
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.toLowerCase(java.util.Locale.ROOT).startsWith("application/json")) {
            sendResponse(exchange, 415, "{\"error\":\"Content-Type must be application/json\"}", "application/json; charset=UTF-8");
            return;
        }
        byte[] body = exchange.getRequestBody().readAllBytes();
        if (body.length == 0) {
            sendResponse(exchange, 400, "{\"error\":\"Empty JSON body\"}", "application/json; charset=UTF-8");
            return;
        }
        String payload = new String(body, StandardCharsets.UTF_8);
        String response = "{\"received\":true,\"sizeBytes\":" + body.length + ",\"payload\":" + payload + "}";
        sendResponse(exchange, 200, response, "application/json; charset=UTF-8");
    }

    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "GET");
            sendResponse(exchange, 405, "Only GET is allowed\n", "text/plain; charset=UTF-8");
            return;
        }
        sendResponse(exchange, 200, "Server is alive\n", "text/plain; charset=UTF-8");
    }

    private void sendResponse(HttpExchange exchange, int status, String message, String type) throws IOException {
        byte[] responseBytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", type);
        exchange.sendResponseHeaders(status, responseBytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBytes);
        } finally {
            exchange.close();
        }
    }
}
