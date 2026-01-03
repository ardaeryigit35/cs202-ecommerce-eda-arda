import java.sql.*;

public class PaymentService {

    public static boolean createPayment(int orderId, double amount) {

        String sql = """
            INSERT INTO Payment
            (OrderID, payment_method, payment_date, payment_amount, payment_status)
            VALUES (?, 'CARD', CURRENT_DATE, ?, 'DONE')
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setDouble(2, amount);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean cancelPayment(Connection conn, int orderId) {

        String sql = """
        UPDATE Payment
        SET payment_status = 'REFUNDED'
        WHERE OrderID = ?
            AND payment_status = 'DONE'
        """;


        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
