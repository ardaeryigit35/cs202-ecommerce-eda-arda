import javax.swing.*;
import java.awt.*;

public class CustomerStatsFrame extends JFrame {

    public CustomerStatsFrame() {

        setTitle("Customer Statistics");
        setSize(450, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        double monthlyTotal = StatisticsService.getMonthlyTotal(UserSession.getUserId());
        String topCategory = StatisticsService.getMostPurchasedCategory(UserSession.getUserId());
        double avgMonthly = StatisticsService.getAverageMonthlyPurchase(UserSession.getUserId());

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        area.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        area.setText(
                "MONTHLY TOTAL PURCHASE:\n" + monthlyTotal + "\n\n" +
                        "MOST PURCHASED CATEGORY:\n" + topCategory + "\n\n" +
                        "AVERAGE MONTHLY PURCHASE:\n" + avgMonthly
        );

        add(area);
        setVisible(true);
    }
}
