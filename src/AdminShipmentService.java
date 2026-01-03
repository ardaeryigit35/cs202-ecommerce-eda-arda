import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminShipmentService {


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


    public static boolean updateShipmentStatus(int shipmentId, String newStatus) {

        String getInfo = """
        SELECT OrderID, shipment_status
        FROM Shipment
        WHERE ShipmentID = ?
    """;

        String updateShipment = "UPDATE Shipment SET shipment_status=? WHERE ShipmentID=?";
        String cancelOrder = "UPDATE OrderTable SET order_status='CANCELLED' WHERE OrderID=?";
        String refundPay = "UPDATE Payment SET payment_status='REFUNDED' WHERE OrderID=? AND payment_status='DONE'";

        try (Connection conn = DB.getConnection()) {
            conn.setAutoCommit(false);

            int orderId;
            String currentStatus;

            try (PreparedStatement ps = conn.prepareStatement(getInfo)) {
                ps.setInt(1, shipmentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) { conn.rollback(); return false; }
                    orderId = rs.getInt("OrderID");
                    currentStatus = rs.getString("shipment_status");
                }
            }

            if ("DELIVERED".equals(currentStatus) || "CANCELLED".equals(currentStatus)) {
                conn.rollback();
                return false;
            }


            if ("PREPARING".equals(newStatus)) {
                conn.rollback();
                return false;
            }


            boolean ok =
                    ("PREPARING".equals(currentStatus) && ("SHIPPED".equals(newStatus) || "CANCELLED".equals(newStatus)))
                            || ("SHIPPED".equals(currentStatus) && ("DELIVERED".equals(newStatus) || "CANCELLED".equals(newStatus)));

            if (!ok) {
                conn.rollback();
                return false;
            }


            try (PreparedStatement ps = conn.prepareStatement(updateShipment)) {
                ps.setString(1, newStatus);
                ps.setInt(2, shipmentId);
                if (ps.executeUpdate() != 1) { conn.rollback(); return false; }
            }


            if ("CANCELLED".equals(newStatus)) {
                try (PreparedStatement ps = conn.prepareStatement(cancelOrder)) {
                    ps.setInt(1, orderId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(refundPay)) {
                    ps.setInt(1, orderId);
                    ps.executeUpdate(); // 0 olabilir, sorun değil
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
