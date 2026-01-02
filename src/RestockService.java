import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//
//public class RestockService {
//
//    public static boolean restockProduct(
//            int productId,
//            int sellerId,
//            int addQty,
//            String note
//    ) {
//
//        String updateProduct = """
//            UPDATE Product
//            SET stock_qty = stock_qty + ?
//            WHERE ProductID = ? AND SellerID = ?
//        """;
//
//        String insertHistory = """
//            INSERT INTO StockHistory (ProductID, SellerID, change_qty, note)
//            VALUES (?, ?, ?, ?)
//        """;
//
//        try (Connection c = DB.getConnection()) {
//            c.setAutoCommit(false);
//
//            try (PreparedStatement ps1 = c.prepareStatement(updateProduct)) {
//                ps1.setInt(1, addQty);
//                ps1.setInt(2, productId);
//                ps1.setInt(3, sellerId);
//
//                if (ps1.executeUpdate() != 1) {
//                    c.rollback();
//                    return false;
//                }
//            }
//
//            try (PreparedStatement ps2 = c.prepareStatement(insertHistory)) {
//                ps2.setInt(1, productId);
//                ps2.setInt(2, sellerId);
//                ps2.setInt(3, addQty);
//                ps2.setString(4, note);
//                ps2.executeUpdate();
//            }
//
//            c.commit();
//            return true;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//}
public class RestockService {

    // ✅ Yeni çekirdek metod: deltaQty (+/-)
    public static boolean adjustStock(
            int productId,
            int sellerId,
            int deltaQty,
            String note
    ) {

        String updateProduct = """
            UPDATE Product
            SET stock_qty = stock_qty + ?
            WHERE ProductID = ?
              AND SellerID = ?
              AND (stock_qty + ?) >= 0
        """;

        String insertHistory = """
            INSERT INTO StockHistory (ProductID, SellerID, change_qty, note)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection c = DB.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps1 = c.prepareStatement(updateProduct)) {
                ps1.setInt(1, deltaQty);
                ps1.setInt(2, productId);
                ps1.setInt(3, sellerId);
                ps1.setInt(4, deltaQty);

                // stok negatife düşecekse update 0 row döner
                if (ps1.executeUpdate() != 1) {
                    c.rollback();
                    return false;
                }
            }

            try (PreparedStatement ps2 = c.prepareStatement(insertHistory)) {
                ps2.setInt(1, productId);
                ps2.setInt(2, sellerId);
                ps2.setInt(3, deltaQty);      // ✅ negative de yazılabilir
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

    // ✅ Var olan restock methodun bunu çağırabilir (UI bozulmasın diye)
    public static boolean restockProduct(int productId, int sellerId, int addQty, String note) {
        if (addQty <= 0) return false;
        return adjustStock(productId, sellerId, addQty, note);
    }

    // ✅ Yeni destock methodu
    public static boolean destockProduct(int productId, int sellerId, int removeQty, String note) {
        if (removeQty <= 0) return false;
        return adjustStock(productId, sellerId, -removeQty, note);
    }
}
