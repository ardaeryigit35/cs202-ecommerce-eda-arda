import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminReviewFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public AdminReviewFrame() {
        setTitle("All Reviews");
        setSize(1050, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new Object[]{"ReviewID", "OrderID", "Product", "Seller", "Customer", "Rating", "Comment", "Date"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadReviews());

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(refreshBtn);
        top.add(closeBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadReviews();
        setVisible(true);
    }

    private void loadReviews() {
        model.setRowCount(0);

        List<AdminReviewService.Row> rows = AdminReviewService.getAllReviews();
        for (AdminReviewService.Row r : rows) {
            model.addRow(new Object[]{
                    r.reviewId,
                    r.orderId,
                    r.productName,
                    r.sellerName,
                    r.customerName,
                    r.rating,
                    r.comment,
                    r.createdAt
            });
        }
    }
}
