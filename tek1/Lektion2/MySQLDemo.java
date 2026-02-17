import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

public class MySQLDemo {
    public static void main(String[] args) {
        //System.out.println("hello");
        Enumeration<Driver> enumeration = DriverManager.getDrivers();
        while (enumeration.hasMoreElements()){
            System.out.println(enumeration.nextElement());
        }
        String url = "jdbc:mysql://localhost:3306/mydb";
        String user = "root";
        String pw = "password";
        try {
            Connection connection = DriverManager.getConnection(url,user, pw);
            System.out.println("forbundet til " + url + " " + connection.getCatalog());
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
