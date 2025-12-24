import java.sql.Connection;

public class TestDB {
    public static void main(String[] args) {
        try (Connection c = DBConnection.getConnection()) {
            System.out.println("DB Connected!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
