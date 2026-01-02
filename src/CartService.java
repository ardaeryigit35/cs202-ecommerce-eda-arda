import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartService {

    // ========================
    // DTO
    // ========================
    public static class CartItem {
        public final int productId;
        public final String productName;
        public final int quantity;
        public final double unitPrice;
        public final double total;

        public CartItem(int productId, String productName, int quantity, double unitPrice, double total) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.total = total;
        }
    }

    // ========================
    // GET OR CREATE CART (single-seller)
    // ========================
    private static int getOrCreateCart(Connection conn, int customerId, int sellerId) throws SQLException {

        String find = """
            SELECT OrderID, SellerID
            FROM OrderTable
            WHERE CustomerID = ? AND order_status = 'CART'
            LIMIT 1
        """;

        try (PreparedStatement ps = conn.prepareStatement(find)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int existingSeller = rs.getInt("SellerID");
                    // existingSeller NULL olamaz çünkü CART yaratırken set ediyoruz
                    if (existingSeller != sellerId) return -1;
                    return rs.getInt("OrderID");
                }
            }
        }

        String create = """
            INSERT INTO OrderTable (CustomerID, SellerID, order_status)
            VALUES (?, ?, 'CART')
        """;

        try (PreparedStatement ps = conn.prepareStatement(create, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customerId);
            ps.setInt(2, sellerId);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) return -1;
                return keys.getInt(1);
            }
        }
    }

    // ========================
    // ADD TO CART
    // returns:
    //  true  -> added/updated
    //  false -> user has cart with another seller OR db error
    // ========================
    public static boolean addToCart(int customerId, int sellerId, int productId, int qty) {

        String priceAndStock = """
            SELECT p_price, stock_qty
            FROM Product
            WHERE ProductID = ? AND SellerID = ? AND is_active = TRUE
        """;

        String upsertItem = """
            INSERT INTO OrderItems (OrderID, ProductID, SellerID, quantity, unit_price)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)
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

            try (PreparedStatement ps = conn.prepareStatement(priceAndStock)) {
                ps.setInt(1, productId);
                ps.setInt(2, sellerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    price = rs.getDouble("p_price");
                    stock = rs.getInt("stock_qty");
                }
            }

            // stock=0 ise ekleme (instruction)
            if (stock <= 0) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(upsertItem)) {
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

    // ========================
    // GET CART ITEMS
    // ========================
    public static List<CartItem> getCartItems(int customerId) {

        String sql = """
            SELECT p.ProductID,
                   p.product_name,
                   oi.quantity,
                   oi.unit_price,
                   (oi.quantity * oi.unit_price) AS total
            FROM OrderTable o
            JOIN OrderItems oi ON oi.OrderID = o.OrderID
            JOIN Product p ON p.ProductID = oi.ProductID AND p.SellerID = oi.SellerID
            WHERE o.CustomerID = ? AND o.order_status = 'CART'
            ORDER BY p.product_name
        """;

        List<CartItem> list = new ArrayList<>();

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new CartItem(
                            rs.getInt("ProductID"),
                            rs.getString("product_name"),
                            rs.getInt("quantity"),
                            rs.getDouble("unit_price"),
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
    // REMOVE ITEM (by product id)
    // ========================
    public static void removeItem(int customerId, int productId) {

        String sql = """
            DELETE oi
            FROM OrderItems oi
            JOIN OrderTable o ON o.OrderID = oi.OrderID
            WHERE o.CustomerID = ?
              AND o.order_status = 'CART'
              AND oi.ProductID = ?
        """;

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.setInt(2, productId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ========================
    // SUBMIT ORDER
    // CART -> PENDING
    // stock check + deduct in ONE transaction
    // ========================
    public static boolean submitOrder(int customerId) {

        String findCart = """
            SELECT OrderID
            FROM OrderTable
            WHERE CustomerID = ? AND order_status = 'CART'
            LIMIT 1
        """;

        String countItems = """
            SELECT COUNT(*) FROM OrderItems WHERE OrderID = ?
        """;

        String checkStock = """
            SELECT p.stock_qty, oi.quantity
            FROM OrderItems oi
            JOIN Product p
              ON p.ProductID = oi.ProductID AND p.SellerID = oi.SellerID
            WHERE oi.OrderID = ?
        """;

        String itemsSql = """
            SELECT ProductID, SellerID, quantity
            FROM OrderItems
            WHERE OrderID = ?
        """;

        String updateStock = """
            UPDATE Product
            SET stock_qty = stock_qty - ?
            WHERE ProductID = ? AND SellerID = ? AND stock_qty >= ?
        """;

        String updateOrder = """
        UPDATE OrderTable
        SET order_status = 'PENDING'
        WHERE OrderID = ?
        """;


        try (Connection conn = DB.getConnection()) {
            conn.setAutoCommit(false);

            int orderId;

            // 1) find cart
            try (PreparedStatement ps = conn.prepareStatement(findCart)) {
                ps.setInt(1, customerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    orderId = rs.getInt("OrderID");
                }
            }

            // 2) empty cart?
            try (PreparedStatement ps = conn.prepareStatement(countItems)) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 3) check stock first
            try (PreparedStatement ps = conn.prepareStatement(checkStock)) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        if (rs.getInt("stock_qty") < rs.getInt("quantity")) {
                            conn.rollback();
                            return false;
                        }
                    }
                }
            }

            // 4) deduct stock
            try (PreparedStatement ps = conn.prepareStatement(itemsSql)) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int pid = rs.getInt("ProductID");
                        int sid = rs.getInt("SellerID");
                        int q = rs.getInt("quantity");

                        try (PreparedStatement upd = conn.prepareStatement(updateStock)) {
                            upd.setInt(1, q);
                            upd.setInt(2, pid);
                            upd.setInt(3, sid);
                            upd.setInt(4, q);
                            int affected = upd.executeUpdate();
                            if (affected == 0) {
                                conn.rollback();
                                return false;
                            }
                        }
                    }
                }
            }

            // 5) update order status
            try (PreparedStatement ps = conn.prepareStatement(updateOrder)) {
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
}
