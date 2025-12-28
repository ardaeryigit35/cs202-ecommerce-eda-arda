import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SellerOrderService {

    // ========================
    // DTO
    // ========================
    public static class SellerOrder {
        public final int orderId;
        public final Timestamp orderDate;
        public final String status;
        public final double totalAmount;
        public final boolean confirmed;

        public SellerOrder(int orderId, Timestamp orderDate,
                           String status, double totalAmount, boolean confirmed) {
            this.orderId = orderId;
            this.orderDate = orderDate;
            this.status = status;
            this.totalAmount = totalAmount;
            this.confirmed = confirmed;
        }
    }

    // ========================
    // GET ORDERS FOR SELLER
    // ========================
    public static List<SellerOrder> getOrdersForSeller(int sellerId) {

        String sql = """
            SELECT o.OrderID,
                   o.order_date,
                   o.order_status,
                   o.confirmed_by_seller,
                   SUM(oi.quantity * oi.unit_price) AS total
            FROM OrderTable o
            JOIN OrderItems oi ON oi.OrderID = o.OrderID
            WHERE o.SellerID = ?
              AND o.order_status <> 'CART'
            GROUP BY o.OrderID, o.order_date, o.order_status, o.confirmed_by_seller
            ORDER BY o.order_date DESC
        """;

        List<SellerOrder> list = new ArrayList<>();

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sellerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SellerOrder(
                            rs.getInt("OrderID"),
                            rs.getTimestamp("order_date"),
                            rs.getString("order_status"),
                            rs.getDouble("total"),
                            rs.getBoolean("confirmed_by_seller")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ========================
    // CONFIRM & SHIP ORDER
    // ========================
    public static boolean shipOrder(int orderId, int sellerId) {

        String update = """
            UPDATE OrderTable
            SET order_status = 'SHIPPED',
                confirmed_by_seller = TRUE
            WHERE OrderID = ?
              AND SellerID = ?
              AND order_status = 'PENDING'
        """;

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(update)) {

            ps.setInt(1, orderId);
            ps.setInt(2, sellerId);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
