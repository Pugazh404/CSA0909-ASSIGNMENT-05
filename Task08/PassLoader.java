import java.sql.*;
import java.util.List;

public class PassLoader {
    /** Loads all passes in one transaction, executing batches of at most 1,000. */
    public static int loadAll(Connection con, List<String[]> passes) throws SQLException {
        String sql = "INSERT INTO bus_pass(pass_id, reg_no, name, route_no, amount) VALUES (?, ?, ?, ?, ?)";
        boolean oldAutoCommit = con.getAutoCommit();
        con.setAutoCommit(false);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int n = 0;
            for (String[] p : passes) {
                if (p == null || p.length != 5) throw new IllegalArgumentException("Each pass needs 5 fields");
                ps.setString(1, p[0]);
                ps.setString(2, p[1]);
                ps.setString(3, p[2]);
                ps.setInt(4, Integer.parseInt(p[3]));
                ps.setDouble(5, Double.parseDouble(p[4]));
                if (ps.getInt(4) < 0 || !Double.isFinite(ps.getDouble(5)) || ps.getDouble(5) < 0)
                    throw new IllegalArgumentException("Invalid route or amount");
                ps.addBatch();
                n++;
                if (n % 1000 == 0) ps.executeBatch();
            }
            ps.executeBatch();
            con.commit();
            return n;
        } catch (SQLException | RuntimeException e) {
            try { con.rollback(); } catch (SQLException rollbackError) { e.addSuppressed(rollbackError); }
            throw e;
        } finally {
            con.setAutoCommit(oldAutoCommit);
        }
    }
}
