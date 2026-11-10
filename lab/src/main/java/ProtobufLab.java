import com.distributed.lab.CustomerProfileProto.CustomerProfile;
import java.util.Arrays;

/** Task 3.2: use the generated protoc stub / Builder / parseFrom. */
public class ProtobufLab {
    public static void main(String[] args) throws Exception {
        CustomerProfile profile = CustomerProfile.newBuilder()
                .setCustomerName("Ahmad Saleh")
                .addAllPurchasedItems(Arrays.asList("Laptop", "Mouse"))
                .addAllItemPrices(Arrays.asList(1200, 25))
                .setIsHolidayDealEligible(true)
                .build();
        byte[] protoPayload = profile.toByteArray();
        System.out.println("Protobuf message size: " + protoPayload.length + " bytes");
        CustomerProfile reconstructed = CustomerProfile.parseFrom(protoPayload);
        System.out.println("Restored name: " + reconstructed.getCustomerName());
        System.out.println("Default city: " + reconstructed.getCity());
        System.out.println("Was city field explicitly set? " + reconstructed.hasCity());
        if (!reconstructed.getCustomerName().equals("Ahmad Saleh"))
            throw new AssertionError("Protobuf round-trip failed");
    }
}
