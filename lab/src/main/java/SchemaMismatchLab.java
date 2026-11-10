import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Task 1.2: controlled demonstration of a receiver expecting numeric prices.
 * This deliberately checks only the lab's small JSON example; it is not a general JSON parser.
 */
public class SchemaMismatchLab {
    private static final Pattern PRICES = Pattern.compile("\\\"prices\\\"\\s*:\\s*\\[([^]]*)\\]");

    private static BigDecimal[] parseNumericPrices(String json) {
        Matcher matcher = PRICES.matcher(json);
        if (!matcher.find()) throw new IllegalArgumentException("Missing prices array");
        String[] rawValues = matcher.group(1).split(",");
        BigDecimal[] values = new BigDecimal[rawValues.length];
        for (int i = 0; i < rawValues.length; i++) {
            String raw = rawValues[i].trim();
            // Reject JSON string values rather than implicitly coercing them.
            if (!raw.matches("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?")) {
                throw new IllegalArgumentException("Schema mismatch: expected JSON number, got " + raw);
            }
            values[i] = new BigDecimal(raw);
        }
        return values;
    }

    public static void main(String[] args) throws Exception {
        String sent = Files.readString(Path.of("lab/prices_sent_as_strings.json"), StandardCharsets.UTF_8);
        String expected = Files.readString(Path.of("lab/prices_expected_as_numbers.json"), StandardCharsets.UTF_8);
        try {
            parseNumericPrices(sent);
            throw new AssertionError("String prices were unexpectedly accepted");
        } catch (IllegalArgumentException mismatch) {
            System.out.println("Sender / receiver mismatch detected: " + mismatch.getMessage());
        }
        BigDecimal[] prices = parseNumericPrices(expected);
        BigDecimal product = BigDecimal.ONE;
        for (BigDecimal price : prices) product = product.multiply(price);
        System.out.println("Correct numeric input works. Product: " + product.toPlainString());
    }
}
