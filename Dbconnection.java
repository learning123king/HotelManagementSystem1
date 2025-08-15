import java.sql.*;

public class Dbconnection {
    static Connection con;

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_reservation", "root", "pass123");
        } catch (Exception e) {
            System.out.println(e);
        }
        return con;
    }
}
