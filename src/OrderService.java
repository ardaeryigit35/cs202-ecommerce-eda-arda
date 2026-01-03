import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderService {


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

    public static List<OrderItem> getOrdersByCustomer(int customerId) {

        String sql = """
            SELECT OrderID,
                   order_date,
                   order_status,
                   total_amount
            FROM OrderTable
            WHERE CustomerID = ?
              AND order_status <> 'CART'
            ORDER BY order_date DESC
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
                            rs.getDouble("total_amount")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


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


    public static class SellerOrderItem {
        public final int orderId;
        public final String customerName;
        public final String orderDate;
        public final String status;
        public final double totalAmount;

        public SellerOrderItem(int orderId,
                               String customerName,
                               String orderDate,
                               String status,
                               double totalAmount) {
            this.orderId = orderId;
            this.customerName = customerName;
            this.orderDate = orderDate;
            this.status = status;
            this.totalAmount = totalAmount;
        }
    }


    public static List<SellerOrderItem> getOrdersBySeller(int sellerId) {

        List<SellerOrderItem> list = new ArrayList<>();

        String sql = """
            SELECT o.OrderID,
                   u.UserName,
                   o.order_date,
                   o.order_status,
                   o.total_amount
            FROM OrderTable o
            JOIN User u ON u.UserID = o.CustomerID
            WHERE o.SellerID = ?
              AND o.order_status <> 'CART'
            ORDER BY o.order_date DESC
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new SellerOrderItem(
                        rs.getInt("OrderID"),
                        rs.getString("UserName"),
                        rs.getString("order_date"),
                        rs.getString("order_status"),
                        rs.getDouble("total_amount")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
