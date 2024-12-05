import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private ShoppingCart cart;
    private JTextArea cartArea;
    private JLabel totalLabel;

    public  MainFrame() {
        cart = new ShoppingCart();
        setTitle("Shopping Cart");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        
        String[] productNames = {"Laptop - ₹999.99", "Smartphone - ₹499.99", "Headphones - ₹199.99", "Coffee Maker - ₹ 79.99" };
        JList<String> productList = new JList<>(productNames);
        JScrollPane productScrollPane = new JScrollPane(productList);
        add(productScrollPane, BorderLayout.WEST);

  
        cartArea = new JTextArea(10, 20);
        cartArea.setEditable(false);
        JScrollPane cartScrollPane = new JScrollPane(cartArea);
        add(cartScrollPane, BorderLayout.CENTER);

     
        totalLabel = new JLabel("Total: ₹0.00");
        add(totalLabel, BorderLayout.SOUTH);

        
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add to Cart");
        JButton checkoutButton = new JButton("Checkout");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = productList.getSelectedIndex();
                if (selectedIndex != -1) {
                    String[] productDetails = productNames[selectedIndex].split(" - \\₹");
                    String name = productDetails[0];
                    double price = Double.parseDouble(productDetails[1]);
                    Product product = new Product(name, price);
                    cart.addProduct(product);
                    updateCartDisplay();
                }
            }
        });

        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainFrame.this, "Total: ₹" + cart.calculateTotal() + "\nThank you for shopping!");
                cart = new ShoppingCart();
                updateCartDisplay();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(checkoutButton);
        add(buttonPanel, BorderLayout.NORTH);
    }

    private void updateCartDisplay() {
        cartArea.setText("");
        for (Product product : cart.getItems()) {
            cartArea.append(product + "\n");
        }
        totalLabel.setText("Total: ₹" + cart.calculateTotal());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}