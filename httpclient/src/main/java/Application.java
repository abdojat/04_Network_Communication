import java.util.Arrays;
import java.util.List;

public class Application {
    private static final String WORKER_ADDRESS_1 = "http://localhost:8080/task";
    private static final String WORKER_ADDRESS_2 = "http://localhost:8081/task";

    public static void main(String[] args) {

        String worker1 = args.length >= 1 ? args[0] : WORKER_ADDRESS_1;
        String worker2 = args.length >= 2 ? args[1] : WORKER_ADDRESS_2;
        Aggregator aggregator = new Aggregator();
        String task1 = "10,200";
        String task2 = "123456789,100000000000000,700000002342343";

        String headers = "X-Debug: true";

        List<String> results = aggregator.sendTasksToWorkers(Arrays.asList(worker1, worker2),
                Arrays.asList(task1, task2));

        for (String result : results) {
            System.out.println(result);
        }

        List<String> resultsWithHeaders = aggregator.sendTasksToWorkers(Arrays.asList(worker1, worker2),
                Arrays.asList(task1, task2), headers);

        for (String result : resultsWithHeaders) {
            System.out.println(result);
        }
    }
}
