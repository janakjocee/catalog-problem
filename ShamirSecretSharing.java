import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

    // Helper method to decode a value in any base
    public static long decodeValue(String value, int base) {
        return Long.parseLong(value, base);
    }

    // Lagrange Interpolation to find the constant term (c)
    public static double lagrangeInterpolation(List<Point> points) {
        double constantTerm = 0;
        int n = points.size();

        for (int i = 0; i < n; i++) {
            double xi = points.get(i).x;
            double yi = points.get(i).y;
            double li = 1;

            for (int j = 0; j < n; j++) {
                if (i != j) {
                    double xj = points.get(j).x;
                    li *= (0 - xj) / (xi - xj);  // Evaluate at x = 0 to find the constant term
                }
            }
            constantTerm += yi * li;
        }
        return constantTerm;
    }

    // Method to solve for the constant term of the polynomial
    public static double solvePolynomial(String filePath) throws IOException {
        // Read and parse the JSON input from the file
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject data = new JSONObject(content);

        int n = data.getJSONObject("keys").getInt("n");
        int k = data.getJSONObject("keys").getInt("k");

        List<Point> points = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            String key = String.valueOf(i);
            if (data.has(key)) {
                JSONObject pointData = data.getJSONObject(key);
                int base = pointData.getInt("base");
                String value = pointData.getString("value");
                
                long decodedY = decodeValue(value, base);
                points.add(new Point(i, decodedY));
            }
        }

        // Ensure we have enough points for interpolation
        if (points.size() < k) {
            throw new IllegalArgumentException("Not enough points for interpolation");
        }

        // Use Lagrange interpolation to find the constant term 'c'
        return lagrangeInterpolation(points.subList(0, k));
    }

    public static void main(String[] args) throws IOException {
        // Test the function with both test cases
        String testCase1 = "test_case_1.json";  // Path to the first test case JSON file
        String testCase2 = "test_case_2.json";  // Path to the second test case JSON file

        // Solve for both test cases
        System.out.println("Constant term for test case 1: " + solvePolynomial(testCase1));
        System.out.println("Constant term for test case 2: " + solvePolynomial(testCase2));
    }
}

// Helper class to represent a point (x, y)
class Point {
    int x;
    long y;

    public Point(int x, long y) {
        this.x = x;
        this.y = y;
    }
}
