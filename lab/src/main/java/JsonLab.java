import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/** Task 1.1: send the provided JSON to the adapted original HTTP server. */
public class JsonLab {
    public static void main(String[] args) throws Exception {
        Path input = Path.of(args.length >= 1 ? args[0] : "lab/customer_request.json");
        String endpoint = args.length >= 2 ? args[1] : "http://localhost:8080/api/analyze";
        byte[] requestBytes = Files.readAllBytes(input);
        HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(requestBytes))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(
                request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        System.out.println("JSON bytes sent: " + requestBytes.length);
        System.out.println("HTTP status: " + response.statusCode());
        System.out.println("Server reply: " + response.body());
        if (response.statusCode() != 200 || !response.body().contains("\"received\":true"))
            throw new AssertionError("HTTP JSON exchange failed");
    }
}
