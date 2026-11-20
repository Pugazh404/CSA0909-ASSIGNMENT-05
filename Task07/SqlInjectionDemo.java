import java.sql.*;
public class SqlInjectionDemo {
    public static void main(String[] args) throws Exception {
        String url = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/assignment5");
        String user = System.getenv().getOrDefault("DB_USER", "root");
        String password = System.getenv().getOrDefault("DB_PASSWORD", "");
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String payload = "' OR '1'='1' -- ";
            String unsafe = "SELECT id FROM users WHERE id='" + payload + "' AND pwd='wrong'";
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(unsafe)) {
                System.out.println("Unsafe accepted: " + rs.next());
            }
            String safe = "SELECT id FROM users WHERE id=? AND pwd=?";
            try (PreparedStatement ps = con.prepareStatement(safe)) {
                ps.setString(1, payload); ps.setString(2, "wrong");
                try (ResultSet rs = ps.executeQuery()) {
                    System.out.println("PreparedStatement accepted: " + rs.next());
                }
            }
        }
    }
}
