import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/** Report question 2: same four common fields in all three formats. */
public class ComparisonLab {
    public static void main(String[] args) throws Exception {
        String json = Files.readString(Path.of("lab/same_data_for_comparison.json"), StandardCharsets.UTF_8).trim();
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        CustomerProfile source = new CustomerProfile("Ahmad Saleh", Arrays.asList("Laptop", "Mouse"),
                new int[]{1200, 25}, true, "NOT_TRANSMITTED");
        byte[] javaBytes = JavaSerializationLab.serialize(source);
        com.distributed.lab.CustomerProfileProto.CustomerProfile proto =
                com.distributed.lab.CustomerProfileProto.CustomerProfile.newBuilder()
                .setCustomerName("Ahmad Saleh")
                .addAllPurchasedItems(Arrays.asList("Laptop", "Mouse"))
                .addAllItemPrices(Arrays.asList(1200, 25))
                .setIsHolidayDealEligible(true)
                .build();
        byte[] protobufBytes = proto.toByteArray();
        System.out.println("Comparison (same common fields, no optional city, UTF-8 compact JSON):");
        System.out.printf("JSON: %d bytes%n", jsonBytes.length);
        System.out.printf("Java Serialization: %d bytes%n", javaBytes.length);
        System.out.printf("Protocol Buffers: %d bytes%n", protobufBytes.length);
        System.out.println("Note: sizes depend on schema, data and encoding; Protobuf does not provide encryption.");
    }
}
