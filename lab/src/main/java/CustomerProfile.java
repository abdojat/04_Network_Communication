import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/** Task 2.1: ordinary fields are serialized; static and transient fields are not. */
public class CustomerProfile implements Serializable {
    private static final long serialVersionUID = 1L;
    public static String systemVersion = "v2.4";
    private transient String tempDiscountToken;

    private String customerName;
    private List<String> purchasedItems;
    private int[] itemPrices;
    private boolean isHolidayDealEligible;
    private Address address; // For task 2.3; null in the normal experiment.

    // Supplied by the PDF; Serializable itself does not require this constructor.
    public CustomerProfile() { }

    public CustomerProfile(String customerName, List<String> purchasedItems,
                           int[] itemPrices, boolean isHolidayDealEligible, String token) {
        this.customerName = customerName;
        this.purchasedItems = purchasedItems;
        this.itemPrices = itemPrices;
        this.isHolidayDealEligible = isHolidayDealEligible;
        this.tempDiscountToken = token;
    }

    public void setAddress(Address address) { this.address = address; }
    public String getTempDiscountToken() { return tempDiscountToken; }
    public String getCustomerName() { return customerName; }
    public Address getAddress() { return address; }

    @Override public String toString() {
        return "CustomerProfile{" +
                "systemVersion='" + systemVersion + '\'' +
                ", tempDiscountToken='" + tempDiscountToken + '\'' +
                ", customerName='" + customerName + '\'' +
                ", purchasedItems=" + purchasedItems +
                ", itemPrices=" + Arrays.toString(itemPrices) +
                ", isHolidayDealEligible=" + isHolidayDealEligible +
                ", address=" + address + '}';
    }
}
