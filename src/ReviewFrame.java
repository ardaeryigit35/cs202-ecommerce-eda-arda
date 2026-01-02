import javax.swing.*;
import java.awt.*;

public class ReviewFrame extends JFrame {

    public ReviewFrame(
            int orderId,
            int productId,
            int sellerId
    ) {

        setTitle("Leave Review");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JComboBox<Integer> ratingBox =
                new JComboBox<>(new Integer[]{1,2,3,4,5});
        JTextArea commentArea = new JTextArea(5,20);

        JButton submitBtn = new JButton("Submit Review");

        submitBtn.addActionListener(e -> {
            try {
                boolean ok = ReviewService.addReview(
                        orderId,
                        productId,
                        sellerId,
                        UserSession.getUserId(),
                        (Integer) ratingBox.getSelectedItem(),
                        commentArea.getText()
                );

                if (ok) {
                    JOptionPane.showMessageDialog(this, "Review submitted.");
                    dispose();
                } else {
                    // ok=false ama exception gelmediyse: genel başarısızlık
                    JOptionPane.showMessageDialog(
                            this,
                            "Review could not be submitted.",
                            "Review Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

            } catch (IllegalStateException ex) {
                // ✅ duplicate review gibi business-rule hatası
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Review Error",
                        JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "Review could not be submitted.",
                        "Review Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });


        JPanel p = new JPanel(new GridLayout(5,1,8,8));
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        p.add(new JLabel("Rating:"));
        p.add(ratingBox);
        p.add(new JLabel("Comment:"));
        p.add(new JScrollPane(commentArea));
        p.add(submitBtn);

        add(p);
        setVisible(true);
    }
}
