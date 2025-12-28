import javax.swing.*;
import java.awt.*;

public class AdminStatsFrame extends JFrame {

    public AdminStatsFrame() {

        setTitle("System Statistics");
        setSize(450, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        double totalSales = StatisticsService.getTotalSales();
        String topSeller = StatisticsService.getTopSeller();
        String topCategory = StatisticsService.getTopSellingCategory();
        String mostPopularItem = StatisticsService.getMostPopularItem();

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        area.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        area.setText(
                "TOTAL SALES:\n" + totalSales + "\n\n" +
                        "TOP SELLER:\n" + topSeller + "\n\n" +
                        "TOP-SELLING CATEGORY:\n" + topCategory + "\n\n" +
                        "MOST POPULAR ITEM:\n" + mostPopularItem
        );

        add(area);
        setVisible(true);
    }
}
