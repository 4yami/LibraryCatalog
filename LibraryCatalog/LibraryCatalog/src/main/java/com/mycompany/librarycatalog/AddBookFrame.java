package com.mycompany.librarycatalog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Paths;

public class AddBookFrame extends JFrame {

    private static final String FILE_PATH = "src/main/java/com/mycompany/librarycatalog/data/BookData.json";

    private HomeFrame homeFrame;  // Reference to the HomeFrame
    private JTable catalogTable;  // Reference to the JTable in HomeFrame
    private JLabel bookIdLabel;
    private JTextField isbnField, titleField, authorField, publisherField, yearField, copyIdField, priceField, languageField;
    private JComboBox<String> genreDropdown;

    public AddBookFrame(HomeFrame homeFrame, JTable catalogTable) {
        this.homeFrame = homeFrame;  // Store the reference
        this.catalogTable = catalogTable;  // Store the reference

        setTitle("Add New Book");
        setSize(400, 400);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Book ID (Auto-generated)
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Book ID:"), gbc);
        gbc.gridx = 1;
        bookIdLabel = new JLabel(String.valueOf(getNextBookId()));
        panel.add(bookIdLabel, gbc);

        // Create input fields using a helper method
        createTextField(panel, "ISBN:", gbc, 1, isbnField = new JTextField(20));
        createTextField(panel, "Title:", gbc, 2, titleField = new JTextField(20));
        createTextField(panel, "Author:", gbc, 3, authorField = new JTextField(20));
        createTextField(panel, "Publisher:", gbc, 4, publisherField = new JTextField(20));
        createTextField(panel, "Year:", gbc, 5, yearField = new JTextField(20));
        createDropdown(panel, "Genre:", gbc, 6);
        createTextField(panel, "Copy ID:", gbc, 7, copyIdField = new JTextField(20));
        createTextField(panel, "Price:", gbc, 8, priceField = new JTextField(20));
        createTextField(panel, "Language:", gbc, 9, languageField = new JTextField(20));

        // Save Button
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton saveButton = new JButton("Save");
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(Color.DARK_GRAY);
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);
        panel.add(saveButton, gbc);

        saveButton.addActionListener(e -> validateAndSave());

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void createTextField(JPanel panel, String label, GridBagConstraints gbc, int row, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void createDropdown(JPanel panel, String label, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        String[] genres = {"Biography & Autobiography", "Fantasy", "Fiction", "Historical Fiction",
            "Horror", "Mystery", "Non-Fiction", "Romance", "Science Fiction", "Thriller"};
        genreDropdown = new JComboBox<>(genres);
        genreDropdown.setPreferredSize(new Dimension(200, 25));
        panel.add(genreDropdown, gbc);
    }

    private int getNextBookId() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            List<Book> books = new Gson().fromJson(reader, new TypeToken<List<Book>>() {
            }.getType());

            if (books.isEmpty()) {
                return 1; // If no books exist, return 1 as the next ID
            }

            // Find the highest bookId
            int highestBookId = books.stream()
                    .mapToInt(Book::getBookId) // Get the bookId of each book
                    .max() // Find the maximum bookId
                    .orElse(0);  // Default to 0 if the list is empty (it shouldn't be, due to the check above)

            return highestBookId + 1;  // Return the next bookId by adding 1 to the highest bookId
        } catch (IOException e) {
            return 1;  // If there's an error, return 1 as the default next bookId
        }
    }

    private void validateAndSave() {
        String isbn = isbnField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String publisher = publisherField.getText().trim();
        String yearStr = yearField.getText().trim();
        String copyIdStr = copyIdField.getText().trim();
        String priceStr = priceField.getText().trim();
        String language = languageField.getText().trim();
        String genre = (String) genreDropdown.getSelectedItem();

        // Check if any field is empty
        if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || publisher.isEmpty()
                || yearStr.isEmpty() || copyIdStr.isEmpty() || priceStr.isEmpty() || language.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate integer fields (Year, Copy ID)
        int year, copyId, bookId;
        try {
            // Ensure the year is a 4-digit number
            if (!yearStr.matches("\\d{4}")) {
                throw new NumberFormatException("Year must be exactly 4 digits");
            }

            year = Integer.parseInt(yearStr);
            copyId = Integer.parseInt(copyIdStr);
            bookId = getNextBookId();  // Auto-generated
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Year must be a valid 4-digit number, and Copy ID must be an integer", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate double field (Price)
        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) {
                JOptionPane.showMessageDialog(this, "Price cannot be negative", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a valid decimal number", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create the new book object
        Book newBook = new Book(bookId, isbn, title, author, publisher, year, genre, copyId, price, language);

        // Save the book
        saveBook(newBook);

        // Refresh the catalog table in HomeFrame
        homeFrame.refreshTable(catalogTable);

        // Show a success message
        JOptionPane.showMessageDialog(this, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        dispose();  // Close the AddBookFrame
    }

    private void saveBook(Book newBook) {
        List<Book> books = new ArrayList<>();
        try (FileReader reader = new FileReader(FILE_PATH)) {
            books = new Gson().fromJson(reader, new TypeToken<List<Book>>() {
            }.getType());
        } catch (IOException ignored) {
        }
        books.add(newBook);

        try (Writer writer = new FileWriter(FILE_PATH)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(books, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
