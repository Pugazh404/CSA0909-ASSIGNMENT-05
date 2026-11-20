import java.nio.file.*;
import java.sql.*;
import java.util.*;

public class MigratePasses {
    public static void main(String[] args) throws Exception {
        Path source=Path.of(args.length>0?args[0]:"passes.csv");
        String url=System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/assignment5");
        String user=System.getenv().getOrDefault("DB_USER","root");
        String pwd=System.getenv().getOrDefault("DB_PASSWORD","");
        List<String[]> valid=new ArrayList<>();
        List<String> rejected=new ArrayList<>();
        int lineNo=0;
        for(String line:Files.readAllLines(source)) {
            lineNo++;
            String[] p=line.split(",",-1);
            try {
                if(p.length!=5 || p[0].isBlank() || p[1].isBlank() || p[2].isBlank())
                    throw new IllegalArgumentException("wrong field count or empty field");
                int route=Integer.parseInt(p[3]); double amount=Double.parseDouble(p[4]);
                if(route<0 || !Double.isFinite(amount) || amount<0) throw new IllegalArgumentException("invalid numeric value");
                valid.add(p);
            } catch(RuntimeException ex) { rejected.add("Line "+lineNo+": "+ex.getMessage()+" ["+line+"]"); }
        }
        try(Connection con=DriverManager.getConnection(url,user,pwd)) {
            int count=PassLoader.loadAll(con,valid);
            System.out.println("Successfully migrated: "+count);
            DatabaseMetaData md=con.getMetaData();
            System.out.println("Database: "+md.getDatabaseProductName());
            System.out.println("Driver: "+md.getDriverName()+" "+md.getDriverVersion());
            try(ResultSet tables=md.getTables(con.getCatalog(),null,"%",new String[]{"TABLE"})) {
                System.out.println("Tables:");
                while(tables.next()) System.out.println(" - "+tables.getString("TABLE_NAME"));
            }
        }
        System.out.println("Rejected records: "+rejected.size());
        for(String issue:rejected) System.out.println(issue);
    }
}
