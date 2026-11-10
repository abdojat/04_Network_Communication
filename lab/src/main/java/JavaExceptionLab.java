import java.io.NotSerializableException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/** Task 2.3: non-serializable nested field + binary format inspection. */
public class JavaExceptionLab {
    public static void main(String[] args) throws Exception {
        CustomerProfile customer = new CustomerProfile("Ahmad Saleh",
                Arrays.asList("Laptop", "Mouse"), new int[]{1200, 25}, true, "HIDDEN");
        customer.setAddress(new Address("Damascus"));
        try {
            JavaSerializationLab.serialize(customer);
            throw new AssertionError("Expected a NotSerializableException");
        } catch (NotSerializableException expected) {
            System.out.println("Expected exception: " + expected.getClass().getSimpleName());
            System.out.println("Non-serializable class: " + expected.getMessage());
        }
        customer.setAddress(null);
        byte[] payload = JavaSerializationLab.serialize(customer);
        String byteView = new String(payload, StandardCharsets.ISO_8859_1);
        System.out.println("Binary contains class name CustomerProfile: " + byteView.contains("CustomerProfile"));
        System.out.println("Binary contains field name customerName: " + byteView.contains("customerName"));
        System.out.println("This format is not encrypted; do not deserialize untrusted data.");
    }
}
