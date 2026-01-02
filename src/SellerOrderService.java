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

        public SellerOrder(int orderId,
                           Timestamp orderDate,
                           String status,
                           double totalAmount,
                           boolean confirmed) {
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

        // ✅ TOTAL SADECE OrderTable.total_amount
        String sql = """
            SELECT o.OrderID,
                   o.order_date,
                   o.order_status,
                   o.confirmed_by_seller,
                   o.total_amount
            FROM OrderTable o
            WHERE o.SellerID = ?
              AND o.order_status <> 'CART'
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
                            rs.getDouble("total_amount"),
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
    // CONFIRM ORDER (MARK AS PAID)
    // ========================
    public static boolean confirmOrder(int orderId, int sellerId) {

        // ✅ İNDİRİMLİ TOTAL BURADAN OKUNUR
        String totalSql = """
            SELECT total_amount
            FROM OrderTable
            WHERE OrderID = ?
              AND SellerID = ?
              AND order_status = 'PENDING'
        """;

        String updateOrder = """
            UPDATE OrderTable
            SET order_status = 'PAID',
                confirmed_by_seller = TRUE
            WHERE OrderID = ?
              AND SellerID = ?
              AND order_status = 'PENDING'
        """;

        String paySql = """
            INSERT INTO Payment
            (OrderID, payment_method, payment_date, payment_amount, payment_status)
            VALUES (?, 'CARD', CURRENT_DATE, ?, 'DONE')
        """;

        try (Connection conn = DB.getConnection()) {
            conn.setAutoCommit(false);

            double totalAmount;

            // 1️⃣ total_amount al
            try (PreparedStatement ps = conn.prepareStatement(totalSql)) {
                ps.setInt(1, orderId);
                ps.setInt(2, sellerId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    totalAmount = rs.getDouble("total_amount");
                }
            }

            // 2️⃣ order → PAID
            int updated;
            try (PreparedStatement ps = conn.prepareStatement(updateOrder)) {
                ps.setInt(1, orderId);
                ps.setInt(2, sellerId);
                updated = ps.executeUpdate();
            }

            if (updated != 1) {
                conn.rollback();
                return false;
            }

            // 3️⃣ payment oluştur (İNDİRİMLİ TUTAR)
            try (PreparedStatement ps = conn.prepareStatement(paySql)) {
                ps.setInt(1, orderId);
                ps.setDouble(2, totalAmount);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========================
    // SHIP ORDER
    // ========================
    public static boolean shipOrder(int orderId, int sellerId) {

        String update = """
            UPDATE OrderTable
            SET order_status = 'SHIPPED'
            WHERE OrderID = ?
              AND SellerID = ?
              AND order_status = 'PAID'
              AND confirmed_by_seller = TRUE
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

    // ========================
    // MARK AS DELIVERED
    // ========================
    public static boolean markAsDelivered(int orderId, int sellerId) {

        String sql = """
            UPDATE OrderTable
            SET order_status = 'DELIVERED'
            WHERE OrderID = ?
              AND SellerID = ?
              AND order_status = 'SHIPPED'
        """;

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setInt(2, sellerId);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
