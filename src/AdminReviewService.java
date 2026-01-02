import java.sql.*;
import java.util.*;

public class AdminReviewService {

    public static class Row {
        public final int reviewId;
        public final int orderId;
        public final int productId;
        public final String productName;
        public final int sellerId;
        public final String sellerName;
        public final int customerId;
        public final String customerName;
        public final int rating;
        public final String comment;
        public final Timestamp createdAt;

        public Row(int reviewId, int orderId, int productId, String productName,
                   int sellerId, String sellerName, int customerId, String customerName,
                   int rating, String comment, Timestamp createdAt) {
            this.reviewId = reviewId;
            this.orderId = orderId;
            this.productId = productId;
            this.productName = productName;
            this.sellerId = sellerId;
            this.sellerName = sellerName;
            this.customerId = customerId;
            this.customerName = customerName;
            this.rating = rating;
            this.comment = comment;
            this.createdAt = createdAt;
        }
    }

    public static List<Row> getAllReviews() {
        // Eğer tablolar/kolonlar sende farklıysa söyle: hızlıca uyarlayayım.
        String sql = """
            SELECT r.ReviewID,
                   r.OrderID,
                   r.ProductID,
                   p.product_name,
                   r.SellerID,
                   su.UserName AS seller_name,
                   r.CustomerID,
                   cu.UserName AS customer_name,
                   r.rating,
                   r.comment,
                   r.created_at
            FROM Review r
            JOIN Product p
              ON p.ProductID = r.ProductID AND p.SellerID = r.SellerID
            JOIN User su ON su.UserID = r.SellerID
            JOIN User cu ON cu.UserID = r.CustomerID
            ORDER BY r.created_at DESC
        """;

        List<Row> rows = new ArrayList<>();

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rows.add(new Row(
                        rs.getInt("ReviewID"),
                        rs.getInt("OrderID"),
                        rs.getInt("ProductID"),
                        rs.getString("product_name"),
                        rs.getInt("SellerID"),
                        rs.getString("seller_name"),
                        rs.getInt("CustomerID"),
                        rs.getString("customer_name"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows;
    }
}
