import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartService {

    public static class CartItem {
        public final int productId;
        public final String productName;
        public final int quantity;
        public final double unitPrice;
        public final double total;

        public CartItem(int productId,
                        String productName,
                        int quantity,
                        double unitPrice,
                        double total) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.total = total;
        }
    }

    public static class DiscountResult {
        public final int discountId;
        public final int percent;

        public DiscountResult(int discountId, int percent) {
            this.discountId = discountId;
            this.percent = percent;
        }
    }


    private static int getOrCreateCart(Connection conn,
                                       int customerId,
                                       int sellerId) throws SQLException {

        String find = """
            SELECT OrderID, SellerID
            FROM OrderTable
            WHERE CustomerID = ?
              AND order_status = 'CART'
            LIMIT 1
        """;

        try (PreparedStatement ps = conn.prepareStatement(find)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (rs.getInt("SellerID") != sellerId) return -1;
                return rs.getInt("OrderID");
            }
        }


        String create = """
            INSERT INTO OrderTable (CustomerID, SellerID, order_status)
            VALUES (?, ?, 'CART')
        """;

        try (PreparedStatement ps =
                     conn.prepareStatement(create, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, customerId);
            ps.setInt(2, sellerId);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (!keys.next()) return -1;
            return keys.getInt(1);
        }
    }

    public static boolean addToCart(int customerId,
                                    int sellerId,
                                    int productId,
                                    int qty) {

        String productSql = """
            SELECT p_price, stock_qty
            FROM Product
            WHERE ProductID = ?
              AND SellerID = ?
              AND is_active = TRUE
        """;

        String upsert = """
            INSERT INTO OrderItems
            (OrderID, ProductID, SellerID, quantity, unit_price)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)
        """;

        String discount_percent_getter = """
            SELECT discount_percent
            FROM discountcode
            
        """;

        try (Connection conn = DB.getConnection()) {
            conn.setAutoCommit(false);

            int cartId = getOrCreateCart(conn, customerId, sellerId);
            if (cartId == -1) {
                conn.rollback();
                return false;
            }

            double price;
            int stock;

            try (PreparedStatement ps = conn.prepareStatement(productSql)) {
                ps.setInt(1, productId);
                ps.setInt(2, sellerId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    conn.rollback();
                    return false;
                }

                price = rs.getDouble("p_price");
                stock = rs.getInt("stock_qty");
            }

            if (stock < qty) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(upsert)) {
                ps.setInt(1, cartId);
                ps.setInt(2, productId);
                ps.setInt(3, sellerId);
                ps.setInt(4, qty);
                ps.setDouble(5, price);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static List<CartItem> getCartItems(int customerId) {

        String sql = """
            SELECT p.ProductID,
                   p.product_name,
                   oi.quantity,
                   oi.unit_price,
                   (oi.quantity * oi.unit_price) AS total
            FROM OrderTable o
            JOIN OrderItems oi ON oi.OrderID = o.OrderID
            JOIN Product p ON p.ProductID = oi.ProductID
                          AND p.SellerID = oi.SellerID
            WHERE o.CustomerID = ?
              AND o.order_status = 'CART'
            ORDER BY p.product_name
        """;

        List<CartItem> list = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new CartItem(
                        rs.getInt("ProductID"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("total")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    public static void removeItem(int customerId, int productId) {

        String sql = """
            DELETE oi
            FROM OrderItems oi
            JOIN OrderTable o ON o.OrderID = oi.OrderID
            WHERE o.CustomerID = ?
              AND o.order_status = 'CART'
              AND oi.ProductID = ?
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.setInt(2, productId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static DiscountResult validateDiscountCode(int customerId, String code) {

        String findSeller = """
            SELECT SellerID
            FROM OrderTable
            WHERE CustomerID = ?
              AND order_status = 'CART'
            LIMIT 1
        """;

        String sql = """
            SELECT DiscountID, discount_percent
            FROM DiscountCode
            WHERE SellerID = ?
              AND code = ?
              AND is_active = TRUE
              AND usage_left > -1
        """;

        try (Connection c = DB.getConnection()) {

            int sellerId;

            try (PreparedStatement ps = c.prepareStatement(findSeller)) {
                ps.setInt(1, customerId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) return null;
                sellerId = rs.getInt(1);
            }

            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, sellerId);
                ps.setString(2, code.trim().toUpperCase());
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) return null;

                return new DiscountResult(
                        rs.getInt("DiscountID"),
                        rs.getInt("discount_percent")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static boolean submitOrder(int customerId, Integer discountId) {

        String findCart = """
        SELECT OrderID
        FROM OrderTable
        WHERE CustomerID = ?
          AND order_status = 'CART'
        LIMIT 1
    """;

        String itemsSql = """
        SELECT ProductID, SellerID, quantity, unit_price
        FROM OrderItems
        WHERE OrderID = ?
    """;

        String updateStock = """
        UPDATE Product
        SET stock_qty = stock_qty - ?
        WHERE ProductID = ?
          AND SellerID = ?
          AND stock_qty >= ?
    """;

        String getDiscountPercent = """
        SELECT discount_percent
        FROM DiscountCode
        WHERE DiscountID = ?
          AND is_active = TRUE
          AND usage_left > -1
    """;

        String updateOrderDiscount = """
        UPDATE OrderTable
        SET DiscountID = ?
        WHERE OrderID = ?
    """;

        String decrementUsage = """
        UPDATE DiscountCode
        SET usage_left = usage_left - 1
        WHERE DiscountID = ?
          AND usage_left > -1
    """;

        String updateTotal = """
        UPDATE OrderTable
        SET total_amount = ?
        WHERE OrderID = ?
    """;

        String submitOrder = """
        UPDATE OrderTable
        SET order_status = 'PENDING'
        WHERE OrderID = ?
    """;

        try (Connection conn = DB.getConnection()) {
            conn.setAutoCommit(false);

            int orderId;
            double total = 0.0;


            try (PreparedStatement ps = conn.prepareStatement(findCart)) {
                ps.setInt(1, customerId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    conn.rollback();
                    return false;
                }
                orderId = rs.getInt("OrderID");
            }


            try (PreparedStatement ps = conn.prepareStatement(itemsSql)) {
                ps.setInt(1, orderId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int productId = rs.getInt("ProductID");
                    int sellerId  = rs.getInt("SellerID");
                    int qty       = rs.getInt("quantity");
                    double price  = rs.getDouble("unit_price");

                    total += qty * price;

                    try (PreparedStatement upd = conn.prepareStatement(updateStock)) {
                        upd.setInt(1, qty);
                        upd.setInt(2, productId);
                        upd.setInt(3, sellerId);
                        upd.setInt(4, qty);

                        if (upd.executeUpdate() == 0) {
                            conn.rollback();
                            return false;
                        }
                    }
                }
            }

            if (discountId != null) {
                try (PreparedStatement ps = conn.prepareStatement(getDiscountPercent)) {
                    ps.setInt(1, discountId);
                    ResultSet rs = ps.executeQuery();

                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }

                    int percent = rs.getInt("discount_percent");
                    total = total * (100 - percent) / 100.0;
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(updateOrderDiscount)) {
                if (discountId == null)
                    ps.setNull(1, Types.INTEGER);
                else
                    ps.setInt(1, discountId);

                ps.setInt(2, orderId);
                ps.executeUpdate();
            }

            if (discountId != null) {
                try (PreparedStatement ps = conn.prepareStatement(decrementUsage)) {
                    ps.setInt(1, discountId);
                    if (ps.executeUpdate() == 0) {
                        conn.rollback();
                        return false;
                    }

                }
            }

            try (PreparedStatement ps = conn.prepareStatement(updateTotal)) {
                ps.setDouble(1, total);
                ps.setInt(2, orderId);
                ps.executeUpdate();
            }


            try (PreparedStatement ps = conn.prepareStatement(submitOrder)) {
                ps.setInt(1, orderId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static Integer getSellerIdByProductId(int productId) {
        String sql = "SELECT SellerID FROM Product WHERE ProductID = ? LIMIT 1";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }



}
