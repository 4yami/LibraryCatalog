package com.mycompany.librarycatalog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame implements ActionListener {

    private JTextField adminIDField;
    private JPasswordField passwordField;
    private JButton loginButton;

    // Constructor - initializes and sets up the UI
    public LoginFrame() {
        setTitle("Welcome to Library"); // Set the title of the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the application when the window is closed
        setSize(400, 250); // Set the window size
        setLocationRelativeTo(null); // Center the window on the screen
        setResizable(false); // Prevent resizing

        // Create a main panel with GridBagLayout for flexible UI design
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout()); // Use GridBagLayout for better control of components
        panel.setBackground(new Color(240, 240, 240)); // Set background color

        // Constraints for GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make components expand horizontally

        // Title Label
        JLabel titleLabel = new JLabel("Library Admin Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Set font style and size
        titleLabel.setForeground(new Color(50, 50, 50)); // Set text color

        gbc.gridx = 0; // Column 0
        gbc.gridy = 0; // Row 0
        gbc.gridwidth = 2; // Span across two columns
        panel.add(titleLabel, gbc); // Add title label to the panel

        // Admin ID Label and Input Field
        JLabel adminIDLabel = new JLabel("Admin ID:");
        adminIDLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font
        adminIDField = new JTextField(15); // Create text field with 15 columns
        adminIDField.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font size

        gbc.gridy = 1; // Move to the next row
        gbc.gridwidth = 1; // Set to one column width
        panel.add(adminIDLabel, gbc); // Add label

        gbc.gridx = 1; // Move to the next column
        panel.add(adminIDField, gbc); // Add text field

        // Password Label and Input Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font
        passwordField = new JPasswordField(15); // Create password field with 15 columns
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font size

        gbc.gridx = 0; // Move back to first column
        gbc.gridy = 2; // Move to next row
        panel.add(passwordLabel, gbc); // Add label

        gbc.gridx = 1; // Move to the next column
        panel.add(passwordField, gbc); // Add password field

        // Add key listener to password field to handle "Enter" key press
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin(); // Trigger login when Enter is pressed
                }
            }
        });

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14)); // Set font style
        loginButton.setBackground(new Color(70, 130, 180)); // Set background color (blue)
        loginButton.setForeground(Color.WHITE); // Set text color
        loginButton.setFocusPainted(false); // Remove focus border
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding
        loginButton.addActionListener(this); // Register action listener for button click

        gbc.gridx = 0; // Move back to first column
        gbc.gridy = 3; // Move to next row
        gbc.gridwidth = 2; // Span across two columns
        panel.add(loginButton, gbc); // Add button to panel

        add(panel); // Add panel to frame
        setVisible(true); // Make the frame visible
    }

    // Handles button click events
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) { // Check if login button was clicked
            performLogin(); // Call login function
        }
    }

    // Perform login validation
    private void performLogin() {
        String adminID = adminIDField.getText(); // Get text from Admin ID field
        String password = new String(passwordField.getPassword()); // Get text from Password field

        if (adminID.isEmpty() || password.isEmpty()) { // Check if fields are empty
            JOptionPane.showMessageDialog(this,
                    "Please enter both Admin ID and Password.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Hardcoded credentials for testing (Replace with a real authentication system)
        if (adminID.equals("1234") && password.equals("1234")) {
            openHomeFrame(); // Open home frame if credentials are correct
            dispose(); // Close the login frame
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Opens the HomeFrame after successful login
    private void openHomeFrame() {
        HomeFrame homeFrame = new HomeFrame();
        homeFrame.showHomeFrame();
    }

    // Main method to start the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new); // Ensure UI updates are handled properly
    }
}
