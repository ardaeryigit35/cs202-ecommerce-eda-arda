import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductDetailFrame extends JFrame {

    private final int productId;

    public ProductDetailFrame(int productId) {
        this.productId = productId;

        setTitle("Product Details");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));


        ProductDetailService.ProductDetail p =
                ProductDetailService.getProductDetail(productId);

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Arial", Font.PLAIN, 14));
        infoArea.setText(
                "Product: " + p.productName + "\n" +
                        "Category: " + p.category + "\n" +
                        "Price: " + p.price + "\n" +
                        "Stock: " + p.stock + "\n\n" +
                        "Description:\n" + p.description
        );

        add(new JScrollPane(infoArea), BorderLayout.NORTH);


        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Customer", "Rating", "Comment", "Date"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);

        List<ProductDetailService.ReviewItem> reviews =
                ProductDetailService.getReviewsForProduct(productId);

        for (ProductDetailService.ReviewItem r : reviews) {
            model.addRow(new Object[]{
                    r.customerName,
                    r.rating,
                    r.comment,
                    r.date
            });
        }

        add(new JScrollPane(table), BorderLayout.CENTER);

        JLabel avgLabel = new JLabel(
                "Average Rating: " +
                        ProductDetailService.getAverageRating(productId)
        );

        avgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avgLabel.setFont(new Font("Arial", Font.BOLD, 14));

        add(avgLabel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
