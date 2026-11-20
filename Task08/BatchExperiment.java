import java.sql.*;
import java.util.*;
public class BatchExperiment {
    static List<String[]> makeRows(int count) {
        List<String[]> rows = new ArrayList<>();
        for (int i=1;i<=count;i++)
            rows.add(new String[]{"P"+i,"REG"+i,"Student"+i,String.valueOf(i%20+1),"4500.00"});
        return rows;
    }
    public static void main(String[] args) throws Exception {
        String url=System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/assignment5");
        String user=System.getenv().getOrDefault("DB_USER","root");
        String pwd=System.getenv().getOrDefault("DB_PASSWORD","");
        try(Connection c=DriverManager.getConnection(url,user,pwd)) {
            for(int size:new int[]{10,1000,10000,100000}) {
                try(Statement s=c.createStatement()){s.executeUpdate("DELETE FROM bus_pass");}
                long start=System.nanoTime();
                int loaded=PassLoader.loadAll(c,makeRows(size));
                long ms=(System.nanoTime()-start)/1_000_000;
                System.out.println("Rows="+loaded+", batch transaction time="+ms+" ms");
            }
        }
    }
}
