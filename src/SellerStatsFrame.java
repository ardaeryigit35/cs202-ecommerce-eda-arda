import javax.swing.*;
import java.awt.*;

public class SellerStatsFrame extends JFrame {

    public SellerStatsFrame() {

        setTitle("Seller Statistics");
        setSize(480, 330);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        int sellerId = UserSession.getUserId();

        double revenue = StatisticsService.getSellerMonthlyRevenue(sellerId);
        String bestProduct = StatisticsService.getBestSellingProduct(sellerId);
        String mostRated = StatisticsService.getMostRatedProduct(sellerId);
        double avgOrder = StatisticsService.getAverageOrderValue(sellerId);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        area.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        area.setText(
                "MONTHLY REVENUE:\n" + revenue + "\n\n" +
                        "BEST SELLING PRODUCT:\n" + bestProduct + "\n\n" +
                        "MOST RATED PRODUCT:\n" + mostRated + "\n\n" +
                        "AVERAGE ORDER VALUE:\n" + avgOrder
        );

        add(area);
        setVisible(true);
    }
}
