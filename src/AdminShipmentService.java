import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminShipmentService {

    // ========================
    // DTO
    // ========================
    public static class ShipmentItem {
        public final int shipmentId;
        public final int orderId;
        public final String shipmentStatus;
        public final String paymentStatus;

        public ShipmentItem(int shipmentId, int orderId,
                            String shipmentStatus, String paymentStatus) {
            this.shipmentId = shipmentId;
            this.orderId = orderId;
            this.shipmentStatus = shipmentStatus;
            this.paymentStatus = paymentStatus;
        }
    }

    // ========================
    // GET ALL SHIPMENTS
    // ========================
    public static List<ShipmentItem> getAllShipments() {

        String sql = """
            SELECT s.ShipmentID,
                   s.OrderID,
                   s.shipment_status,
                   p.payment_status
            FROM Shipment s
            JOIN Payment p ON p.OrderID = s.OrderID
            ORDER BY s.ShipmentID
        """;

        List<ShipmentItem> list = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new ShipmentItem(
                        rs.getInt("ShipmentID"),
                        rs.getInt("OrderID"),
                        rs.getString("shipment_status"),
                        rs.getString("payment_status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ========================
    // UPDATE SHIPMENT STATUS
    // only if payment DONE
    // ========================
    public static boolean updateShipmentStatus(int shipmentId, String newStatus) {

        String sql = """
            UPDATE Shipment s
            JOIN Payment p ON p.OrderID = s.OrderID
            SET s.shipment_status = ?
            WHERE s.ShipmentID = ?
              AND p.payment_status = 'DONE'
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, shipmentId);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            return false;
        }
    }
}
