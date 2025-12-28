import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderService {

    // ========================
    // ORDER LIST DTO
    // ========================
    public static class OrderItem {
        public final int orderId;
        public final Timestamp orderDate;
        public final String status;
        public final double totalAmount;

        public OrderItem(int orderId, Timestamp orderDate,
                         String status, double totalAmount) {
            this.orderId = orderId;
            this.orderDate = orderDate;
            this.status = status;
            this.totalAmount = totalAmount;
        }
    }

    // ========================
    // ORDER PRODUCT DTO (🔴 EKSİKTİ)
    // ========================
    public static class OrderProduct {
        public final int productId;
        public final String productName;
        public final int sellerId;

        public OrderProduct(int productId, String productName, int sellerId) {
            this.productId = productId;
            this.productName = productName;
            this.sellerId = sellerId;
        }
    }

    // ========================
    // GET CUSTOMER ORDERS
    // ========================
    public static List<OrderItem> getOrdersByCustomer(int customerId) {

        String sql = """
            SELECT o.OrderID,
                   o.order_date,
                   o.order_status,
                   SUM(oi.quantity * oi.unit_price) AS total
            FROM OrderTable o
            JOIN OrderItems oi ON oi.OrderID = o.OrderID
            WHERE o.CustomerID = ?
              AND o.order_status <> 'CART'
            GROUP BY o.OrderID, o.order_date, o.order_status
            ORDER BY o.order_date DESC
        """;

        List<OrderItem> list = new ArrayList<>();

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new OrderItem(
                            rs.getInt("OrderID"),
                            rs.getTimestamp("order_date"),
                            rs.getString("order_status"),
                            rs.getDouble("total")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ========================
    // GET PRODUCTS OF AN ORDER (🔴 EKSİKTİ)
    // ========================
    public static List<OrderProduct> getProductsByOrder(int orderId) {

        String sql = """
            SELECT p.ProductID,
                   p.product_name,
                   oi.SellerID
            FROM OrderItems oi
            JOIN Product p
              ON p.ProductID = oi.ProductID
             AND p.SellerID = oi.SellerID
            WHERE oi.OrderID = ?
        """;

        List<OrderProduct> list = new ArrayList<>();

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new OrderProduct(
                            rs.getInt("ProductID"),
                            rs.getString("product_name"),
                            rs.getInt("SellerID")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
