import java.io.*;
import java.util.Arrays;

/** Task 2.2: byte-array serialization and deserialization. */
public class JavaSerializationLab {
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(obj);
        }
        return bytes.toByteArray();
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return in.readObject();
        }
    }

    public static void main(String[] args) throws Exception {
        CustomerProfile.systemVersion = "v2.4";
        CustomerProfile original = new CustomerProfile(
                "Ahmad Saleh", Arrays.asList("Laptop", "Mouse"),
                new int[]{1200, 25}, true, "SECRET-TOKEN-999");
        System.out.println("Before serialization:\n" + original);
        byte[] payload = serialize(original);
        System.out.println("Java serialization size: " + payload.length + " bytes");

        CustomerProfile.systemVersion = "v3.0-UPDATED";
        CustomerProfile reconstructed = (CustomerProfile) deserialize(payload);
        System.out.println("After deserialization:\n" + reconstructed);
        if (reconstructed.getTempDiscountToken() != null)
            throw new AssertionError("transient field should be null");
        if (!"v3.0-UPDATED".equals(CustomerProfile.systemVersion))
            throw new AssertionError("static value should retain the new value");
        System.out.println("CHECK PASS: transient=null, static=v3.0-UPDATED");
    }
}
