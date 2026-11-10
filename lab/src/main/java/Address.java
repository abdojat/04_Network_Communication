/** Deliberately NOT Serializable, to demonstrate NotSerializableException. */
public class Address {
    private final String city;
    public Address(String city) { this.city = city; }
    @Override public String toString() { return city; }
}
