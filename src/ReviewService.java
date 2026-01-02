import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ReviewService {

    public static boolean addReview(
            int orderId,
            int productId,
            int sellerId,
            int customerId,
            int rating,
            String comment
    ) {

        String checkSql = """
            SELECT 1
            FROM OrderTable o
            JOIN OrderItems oi ON oi.OrderID = o.OrderID
            WHERE o.OrderID = ?
              AND o.CustomerID = ?
              AND oi.ProductID = ?
              AND oi.SellerID = ?
              AND o.order_status IN ('SHIPPED','DELIVERED')
            LIMIT 1
        """;

        String insertSql = """
            INSERT INTO Review
            (OrderID, ProductID, SellerID, CustomerID, rating, comment)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection c = DB.getConnection()) {

            // 🔒 VALIDATION
            try (PreparedStatement check = c.prepareStatement(checkSql)) {
                check.setInt(1, orderId);
                check.setInt(2, customerId);
                check.setInt(3, productId);
                check.setInt(4, sellerId);

                ResultSet rs = check.executeQuery();
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(
                            null,
                            "You can only review products you purchased.",
                            "Review Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return false;
                }
            }

            // ✅ INSERT REVIEW
            try (PreparedStatement ps = c.prepareStatement(insertSql)) {
                ps.setInt(1, orderId);
                ps.setInt(2, productId);
                ps.setInt(3, sellerId);
                ps.setInt(4, customerId);
                ps.setInt(5, rating);
                ps.setString(6, comment);
                ps.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            // MySQL duplicate key
            if (e.getErrorCode() == 1062) {
                throw new IllegalStateException(
                        "You cannot submit more than one review for the same order/product."
                );
            }
            e.printStackTrace();
            throw new RuntimeException("Review could not be submitted.");
        }
    }
}
