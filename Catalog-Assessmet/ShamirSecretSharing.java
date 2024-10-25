import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

    public static void main(String[] args) {
        try {
            // Read JSON from file
            String content = new String(Files.readAllBytes(Paths.get("input.json")));
            JSONObject data = new JSONObject(content);
            JSONObject keys = data.getJSONObject("keys");

            int n = keys.getInt("n");
            int k = keys.getInt("k");
            int m = k - 1; // Degree of polynomial

            // Lists to hold (x, y) points
            List<BigInteger> xValues = new ArrayList<>();
            List<BigInteger> yValues = new ArrayList<>();

            // Parse each x, y pair from the JSON object
            for (int i = 1; i <= n; i++) {
                JSONObject point = data.getJSONObject(String.valueOf(i));
                int base = point.getInt("base");
                String value = point.getString("value");

                BigInteger x = BigInteger.valueOf(i);
                BigInteger y = new BigInteger(value, base);

                xValues.add(x);
                yValues.add(y);
            }

            // Calculate the constant term using Lagrange interpolation
            BigInteger constantTerm = lagrangeInterpolation(xValues, yValues, BigInteger.ZERO);

            // Output the constant term
            System.out.println("The constant term c is: " + constantTerm);
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
    }

    /**
     * Perform Lagrange interpolation to find the value at x = 0
     * @param xValues List of x coordinates
     * @param yValues List of y coordinates
     * @param xToEvaluate The x value to evaluate the polynomial at (usually zero for the constant term)
     * @return The evaluated result at xToEvaluate
     */
    public static BigInteger lagrangeInterpolation(List<BigInteger> xValues, List<BigInteger> yValues, BigInteger xToEvaluate) {
        BigInteger result = BigInteger.ZERO;
        int k = xValues.size();

        for (int i = 0; i < k; i++) {
            BigInteger term = yValues.get(i);
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    // Multiply term by (xToEvaluate - x_j) / (x_i - x_j)
                    term = term.multiply(xToEvaluate.subtract(xValues.get(j)))
                               .divide(xValues.get(i).subtract(xValues.get(j)));
                }
            }
            result = result.add(term);
        }
        return result;
    }
}
