import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class MainFrame extends JFrame {
    private ShoppingCart cart;
    private JTextArea cartArea;
    private JLabel totalLabel;
    private JTextField productNameField;
    private JSpinner quantitySpinner;

    public MainFrame() {
        cart = new ShoppingCart();
        setTitle("Shopping Cart");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Color scheme and font
        Color backgroundColor = new Color(240, 240, 240);
        Font mainFont = new Font("Arial", Font.PLAIN, 14);
        setBackground(backgroundColor);

        // Product Input Panel (User enters product details here)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 1, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Enter Product"));
        
        productNameField = new JTextField();
        productNameField.setFont(mainFont);
        productNameField.setBorder(BorderFactory.createTitledBorder("Product Name"));
        
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantitySpinner.setFont(mainFont);
        quantitySpinner.setBorder(BorderFactory.createTitledBorder("Quantity"));
        
        inputPanel.add(productNameField);
        inputPanel.add(quantitySpinner);
        add(inputPanel, BorderLayout.NORTH);  // Add input fields at the top

        // Cart Display Panel (Below the input panel)
        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new BorderLayout(10, 10));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Your Cart"));
        
        cartArea = new JTextArea(15, 25);
        cartArea.setFont(mainFont);
        cartArea.setEditable(false);
        JScrollPane cartScrollPane = new JScrollPane(cartArea);
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);
        
        // Total Label
        totalLabel = new JLabel("Total: ₹0.00", SwingConstants.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        cartPanel.add(totalLabel, BorderLayout.SOUTH);
        
        add(cartPanel, BorderLayout.CENTER);  // Add cart display panel below the input fields

        // Button Panel (For actions like Add, Remove, Checkout)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton addButton = new JButton("Add to Cart");
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setBackground(new Color(102, 204, 255));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setToolTipText("Add product to cart");

        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        checkoutButton.setBackground(new Color(102, 204, 102));
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setToolTipText("Proceed to checkout");

        JButton removeButton = new JButton("Remove from Cart");
        removeButton.setFont(new Font("Arial", Font.BOLD, 14));
        removeButton.setBackground(new Color(255, 102, 102));
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);
        removeButton.setToolTipText("Remove selected product from cart");

        // Add Button Action Listener
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String productName = productNameField.getText().trim();
                int quantity = (Integer) quantitySpinner.getValue();

                if (productName.isEmpty() || !Character.isLetter(productName.charAt(0))) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Enter a valid Product name.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Generate a random price (4-digit number)
                Random random = new Random();
                double price = 100 + random.nextInt(9000); // Random price between ₹1000 and ₹9999
                Product product = new Product(productName, price, quantity);
                cart.addProduct(product);
                updateCartDisplay();
                productNameField.setText("");  // Clear input field after adding product
                quantitySpinner.setValue(1);  // Reset quantity spinner
            }
        });

        // Checkout Button Action Listener (Fake Payment Simulation)
        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cart.getItems().isEmpty()) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Your cart is empty. Please add items before checkout.");
                    return;
                }

                // Collect fake payment details
                String cardNumber = JOptionPane.showInputDialog("Enter card number (simulated):");
                String expiryDate = JOptionPane.showInputDialog("Enter expiry date (MM/YY):");

                FakePaymentSimulator paymentSimulator = new FakePaymentSimulator();
                boolean paymentSuccess = paymentSimulator.processPayment(cardNumber, expiryDate, cart.calculateTotal());

                if (paymentSuccess) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Total: ₹" + String.format("%.2f", cart.calculateTotal()) + "\nThank you for shopping!");
                    cart = new ShoppingCart();
                    updateCartDisplay();
                }
            }
        });

        // Remove Product Button Action Listener (Remove by Quantity)
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String productName = JOptionPane.showInputDialog(MainFrame.this, "Enter product name to remove:");
                if (productName != null && !productName.trim().isEmpty()) {
                    String quantityStr = JOptionPane.showInputDialog(MainFrame.this, "Enter quantity to remove:");
                    try {
                        int quantityToRemove = Integer.parseInt(quantityStr);
                        if (quantityToRemove > 0) {
                            boolean removed = cart.removeProductByNameAndQuantity(productName.trim(), quantityToRemove);
                            if (removed) {
                                JOptionPane.showMessageDialog(MainFrame.this, "Product(s) removed from cart.");
                            } else {
                                JOptionPane.showMessageDialog(MainFrame.this, "Product not found in cart or insufficient quantity.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(MainFrame.this, "Invalid quantity.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(MainFrame.this, "Please enter a valid number for quantity.");
                    }
                    updateCartDisplay();
                }
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(checkoutButton);
        buttonPanel.add(removeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateCartDisplay() {
        cartArea.setText("");
        for (Product product : cart.getItems()) {
            cartArea.append(product + "\n");
        }
        totalLabel.setText("Total: ₹" + String.format("%.2f", cart.calculateTotal()));
    }

    // Fake Payment Simulation Class
    class FakePaymentSimulator {
        public boolean processPayment(String cardNumber, String expiryDate, double totalAmount) {
            // Simple validation for mock payment
            if (cardNumber.length() != 16 || !cardNumber.matches("[0-9]+")) {
                JOptionPane.showMessageDialog(null, "Invalid card number", "Payment Failed", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (expiryDate.isEmpty() || !expiryDate.matches("\\d{2}/\\d{2}")) {
                JOptionPane.showMessageDialog(null, "Invalid expiry date", "Payment Failed", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            // Simulate a successful payment (50% chance)
            if (Math.random() > 0.5) {
                JOptionPane.showMessageDialog(null, "Payment Successful! Amount: ₹" + totalAmount, "Payment Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Payment Failed", "Payment Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
