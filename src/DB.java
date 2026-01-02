import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    private static final String URL =
            "jdbc:mysql://localhost:3306/cs202fall2025project";
    private static final String USER = "root";
    private static final String PASS = "111111"; // kendi şifren

    public static Connection getConnection() throws SQLException {
     //   return DriverManager.getConnection(URL, USER, PASS);
        return getConnection2();
    }
    public static Connection getConnection2() {
        Connection myConn = null;

        try {
            myConn = DriverManager.getConnection(URL, USER, PASS);
            if (myConn != null) {
                System.out.println(" Connected to the database !");
                // Continue to the next steps ...
            }
            else {
                System.out.println(" Failed to make a connection !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return myConn;
    }


}
