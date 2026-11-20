import java.nio.file.*;
import java.util.Arrays;
public class PassStoreTest {
    public static void main(String[] args) throws Exception {
        Path f = Files.createTempFile("pass-test", ".csv");
        PassStore store = new PassStore(f);
        store.add("P01", "REG01", "Asha", 4, 1200);
        Files.writeString(f, "BROKEN\nP02,REG02,Ravi,not-a-route,900\nP03,REG03,Meena,5,1500\n",
                StandardOpenOption.APPEND);
        System.out.println("Existing: " + Arrays.toString(store.find("P03")));
        System.out.println("Missing: " + Arrays.toString(store.find("PX")));
        System.out.println("Damaged P02: " + Arrays.toString(store.find("P02")));\n        System.out.println("Route 4 records: " + store.listByRoute(4).size());
        Files.deleteIfExists(f);
    }
}
