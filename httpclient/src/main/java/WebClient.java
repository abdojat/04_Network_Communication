import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/** Original WebClient, with the two TODO operations implemented. */
public class WebClient {
    private final HttpClient client;

    public WebClient() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public CompletableFuture<String> sendTask(String url, byte[] requestPayload) {
        return sendTask(url, requestPayload, "");
    }

    public CompletableFuture<String> sendTask(String url, byte[] requestPayload, String headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "text/plain; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofByteArray(requestPayload));
        if (headers != null && !headers.isBlank()) {
            for (String line : headers.split("\\r?\\n")) {
                if (line.isBlank()) continue;
                int colon = line.indexOf(':');
                if (colon <= 0) throw new IllegalArgumentException("Header must be 'Name: value': " + line);
                builder.header(line.substring(0, colon).trim(), line.substring(colon + 1).trim());
            }
        }
        return client.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(response -> {
                    if (response.statusCode() < 200 || response.statusCode() >= 300) {
                        throw new IllegalStateException("HTTP " + response.statusCode() + ": " + response.body());
                    }
                    return response.body();
                });
    }
}
