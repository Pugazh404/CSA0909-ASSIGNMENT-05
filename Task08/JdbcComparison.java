import java.sql.*;
import java.util.*;

public class JdbcComparison {
    static final String SQL = "INSERT INTO bus_pass(pass_id,reg_no,name,route_no,amount) VALUES(?,?,?,?,?)";
    static List<String[]> rows(int n) {
        List<String[]> a=new ArrayList<>();
        for(int i=1;i<=n;i++) a.add(new String[]{"P"+i,"R"+i,"Student"+i,"2","4500"});
        return a;
    }
    static void clear(Connection c)throws SQLException {
        try(Statement s=c.createStatement()){s.executeUpdate("DELETE FROM bus_pass");}
    }
    static void set(PreparedStatement p,String[] r)throws SQLException {
        p.setString(1,r[0]);p.setString(2,r[1]);p.setString(3,r[2]);
        p.setInt(4,Integer.parseInt(r[3]));p.setDouble(5,Double.parseDouble(r[4]));
    }
    static long runStatement(Connection c,List<String[]> data)throws SQLException {
        long t=System.nanoTime();
        try(Statement s=c.createStatement()){
            for(String[] r:data) s.executeUpdate("INSERT INTO bus_pass(pass_id,reg_no,name,route_no,amount) VALUES('"+r[0]+"','"+r[1]+"','"+r[2]+"',"+r[3]+","+r[4]+")");
        }
        return (System.nanoTime()-t)/1_000_000;
    }
    static long runPrepared(Connection c,List<String[]> data,boolean tx)throws SQLException {
        boolean old=c.getAutoCommit();if(tx)c.setAutoCommit(false);long t=System.nanoTime();
        try(PreparedStatement p=c.prepareStatement(SQL)){
            for(String[] r:data){set(p,r);p.executeUpdate();}
            if(tx)c.commit();
        }catch(SQLException e){if(tx)c.rollback();throw e;}
        finally{if(tx)c.setAutoCommit(old);}
        return (System.nanoTime()-t)/1_000_000;
    }
    static long runBatch(Connection c,List<String[]> data,boolean tx)throws SQLException {
        boolean old=c.getAutoCommit();if(tx)c.setAutoCommit(false);long t=System.nanoTime();
        try(PreparedStatement p=c.prepareStatement(SQL)){
            int i=0;for(String[] r:data){set(p,r);p.addBatch();if(++i%1000==0)p.executeBatch();}
            p.executeBatch();if(tx)c.commit();
        }catch(SQLException e){if(tx)c.rollback();throw e;}
        finally{if(tx)c.setAutoCommit(old);}
        return (System.nanoTime()-t)/1_000_000;
    }
    public static void main(String[] args)throws Exception{
        String url=System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/assignment5");
        String user=System.getenv().getOrDefault("DB_USER","root");
        String pwd=System.getenv().getOrDefault("DB_PASSWORD","");
        try(Connection c=DriverManager.getConnection(url,user,pwd)){
            for(int n:new int[]{10,1000,10000,100000}){
                List<String[]> d=rows(n);
                clear(c);System.out.println(n+" Statement: "+runStatement(c,d)+" ms");
                clear(c);System.out.println(n+" PreparedStatement auto-commit: "+runPrepared(c,d,false)+" ms");
                clear(c);System.out.println(n+" PreparedStatement transaction: "+runPrepared(c,d,true)+" ms");
                clear(c);System.out.println(n+" Batch auto-commit: "+runBatch(c,d,false)+" ms");
                clear(c);System.out.println(n+" Batch transaction: "+runBatch(c,d,true)+" ms");
            }
        }
    }
}
