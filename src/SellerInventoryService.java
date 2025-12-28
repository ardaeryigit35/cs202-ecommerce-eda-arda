import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SellerInventoryService {

    public static boolean restockProduct(
            int productId,
            int sellerId,
            int addedQty,
            String note
    ) {

        String updateStock = """
            UPDATE Product
            SET stock_qty = stock_qty + ?
            WHERE ProductID = ? AND SellerID = ?
        """;

        String insertHistory = """
            INSERT INTO StockHistory (ProductID, SellerID, change_qty, note)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection c = DB.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps1 = c.prepareStatement(updateStock)) {
                ps1.setInt(1, addedQty);
                ps1.setInt(2, productId);
                ps1.setInt(3, sellerId);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = c.prepareStatement(insertHistory)) {
                ps2.setInt(1, productId);
                ps2.setInt(2, sellerId);
                ps2.setInt(3, addedQty);
                ps2.setString(4, note);
                ps2.executeUpdate();
            }

            c.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
