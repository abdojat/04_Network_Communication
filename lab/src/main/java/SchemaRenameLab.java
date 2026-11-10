import java.util.Arrays;

/** Report question 2: renaming tag 1 at the receiver retains wire compatibility. */
public class SchemaRenameLab {
    public static void main(String[] args) throws Exception {
        com.distributed.lab.CustomerProfileProto.CustomerProfile sender =
                com.distributed.lab.CustomerProfileProto.CustomerProfile.newBuilder()
                .setCustomerName("Ahmad Saleh")
                .addAllPurchasedItems(Arrays.asList("Laptop", "Mouse"))
                .addAllItemPrices(Arrays.asList(1200, 25))
                .setIsHolidayDealEligible(true)
                .build();
        byte[] transmitted = sender.toByteArray();
        com.distributed.lab.CustomerProfileRenamedProto.CustomerProfile receiver =
                com.distributed.lab.CustomerProfileRenamedProto.CustomerProfile.parseFrom(transmitted);
        System.out.println("Receiver field name: client_full_name");
        System.out.println("Receiver reads tag 1: " + receiver.getClientFullName());
        System.out.println("Round-trip succeeded: " + "Ahmad Saleh".equals(receiver.getClientFullName()));
        if (!"Ahmad Saleh".equals(receiver.getClientFullName()))
            throw new AssertionError("Tag 1 failed to deserialize after rename");
    }
}
