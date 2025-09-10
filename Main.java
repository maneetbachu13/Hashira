import java.io.FileReader;
import java.math.BigInteger;
import java.util.*;
import com.google.gson.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java Main <inputfile.json>");
            System.exit(1);
        }

        String filename = args[0];

        // Parse JSON file
        JsonObject root;
        try (FileReader fr = new FileReader(filename)) {
            root = JsonParser.parseReader(fr).getAsJsonObject();
        }

        JsonObject keys = root.getAsJsonObject("keys");
        int n = keys.get("n").getAsInt();
        int k = keys.get("k").getAsInt();

        List<Integer> rootIndices = new ArrayList<>();
        for (String key : root.keySet()) {
            if (!key.equals("keys")) {
                try {
                    int idx = Integer.parseInt(key);
                    rootIndices.add(idx);
                } catch (NumberFormatException ignored) {}
            }
        }
        Collections.sort(rootIndices);

        if (rootIndices.size() < k) {
            System.err.println("Not enough roots in JSON");
            System.exit(1);
        }

        BigInteger[] roots = new BigInteger[k];
        for (int i = 0; i < k; i++) {
            JsonObject rootObj = root.getAsJsonObject(String.valueOf(rootIndices.get(i)));
            String baseStr = rootObj.get("base").getAsString();
            String valStr = rootObj.get("value").getAsString();

            int base = Integer.parseInt(baseStr);

            roots[i] = new BigInteger(valStr, base);
        }


        BigInteger productAll = BigInteger.ONE;
        boolean zeroRoot = false;
        for (BigInteger r : roots) {
            if (r.equals(BigInteger.ZERO)) {
                zeroRoot = true;
                break;
            }
            productAll = productAll.multiply(r);
        }

        BigInteger sumOfProducts = BigInteger.ZERO;
        if (zeroRoot) {
            // If zero root exists, productAll = 0, so sumOfProducts = 0
            sumOfProducts = BigInteger.ZERO;
        } else {
            for (BigInteger r : roots) {
                // productAll / r
                sumOfProducts = sumOfProducts.add(productAll.divide(r));
            }
        }

        // c_1 = (-1)^{k-1} * sumOfProducts
        BigInteger c1 = (k % 2 == 1) ? sumOfProducts : sumOfProducts.negate();

        System.out.println(c1);
    }
}
