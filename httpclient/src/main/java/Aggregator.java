import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Dispatch tasks concurrently across HTTP workers, preserving task order. */
public class Aggregator {
    private final WebClient webClient;

    public Aggregator() { this.webClient = new WebClient(); }

    public List<String> sendTasksToWorkers(List<String> workersAddresses, List<String> tasks) {
        return sendTasksToWorkers(workersAddresses, tasks, "");
    }

    public List<String> sendTasksToWorkers(List<String> workersAddresses, List<String> tasks, String headers) {
        if (workersAddresses == null || workersAddresses.isEmpty())
            throw new IllegalArgumentException("At least one worker URL is required");
        if (tasks == null) throw new IllegalArgumentException("Tasks must not be null");

        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            String workerUrl = workersAddresses.get(i % workersAddresses.size());
            futures.add(webClient.sendTask(workerUrl, tasks.get(i).getBytes(StandardCharsets.UTF_8), headers));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        List<String> results = new ArrayList<>();
        for (CompletableFuture<String> f : futures) results.add(f.join());
        return results;
    }
}
