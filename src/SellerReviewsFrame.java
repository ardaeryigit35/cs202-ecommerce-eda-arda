import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SellerReviewsFrame extends JFrame {

    private final DefaultTableModel model;

    public SellerReviewsFrame() {

        setTitle("Product Reviews");
        setSize(750, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new Object[]{"Product", "Customer", "Rating", "Comment", "Date"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(refreshBtn);
        bottom.add(closeBtn);

        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loadReviews();

        refreshBtn.addActionListener(e -> loadReviews());
        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void loadReviews() {
        model.setRowCount(0);

        List<SellerReviewService.ReviewItem> reviews =
                SellerReviewService.getReviewsForSeller(UserSession.getUserId());

        for (SellerReviewService.ReviewItem r : reviews) {
            model.addRow(new Object[]{
                    r.productName,
                    r.customerName,
                    r.rating,
                    r.comment,
                    r.date
            });
        }
    }
}
