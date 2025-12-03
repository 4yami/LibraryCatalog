package com.mycompany.librarycatalog;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class BookDetailsFrame extends JFrame {

    private static final String FILE_PATH = "src/main/java/com/mycompany/librarycatalog/data/BookData.json";

    private HomeFrame homeFrame;
    private JTable catalogTable;

    private JTextField bookIdField;
    private JTextField isbnField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField publisherField;
    private JTextField yearField;
    private JComboBox<String> genreDropdown;
    private JTextField copyIdField;
    private JTextField priceField;
    private JTextField languageField;

    private static final String[] GENRES = {
        "Biography & Autobiography", "Fantasy", "Fiction", "Historical Fiction",
        "Horror", "Mystery", "Non-Fiction", "Romance", "Science Fiction", "Thriller"
    };

    public BookDetailsFrame(HomeFrame homeFrame, JTable catalogTable, Book book) {
        this.homeFrame = homeFrame;  // Use the passed homeFrame instance
        this.catalogTable = catalogTable;
        setTitle("Book Details");
        setSize(400, 450);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        bookIdField = createEditableField(panel, "Book ID:", gbc, 0, String.valueOf(book.getBookId()));
        isbnField = createEditableField(panel, "ISBN:", gbc, 1, book.getIsbn());
        titleField = createEditableField(panel, "Title:", gbc, 2, book.getTitle());
        authorField = createEditableField(panel, "Author:", gbc, 3, book.getAuthor());
        publisherField = createEditableField(panel, "Publisher:", gbc, 4, book.getPublisher());
        yearField = createEditableField(panel, "Year:", gbc, 5, String.valueOf(book.getYear()));
        genreDropdown = createDropdownField(panel, "Genre:", gbc, 6, book.getGenre());
        genreDropdown.setPreferredSize(new Dimension(200, 25));
        copyIdField = createEditableField(panel, "Copy ID:", gbc, 7, String.valueOf(book.getCopyId()));
        priceField = createEditableField(panel, "Price:", gbc, 8, String.format("%.2f", book.getPrice()));
        languageField = createEditableField(panel, "Language:", gbc, 9, book.getLanguage());

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton saveButton = new JButton("Save");
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(Color.DARK_GRAY); // Blue color
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);
        saveButton.addActionListener(e -> {
            saveBookDetails(book);
            saveButton.setFocusable(false);  // Remove focus
            panel.requestFocusInWindow();    // Shift focus away
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBackground(Color.RED);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        deleteButton.addActionListener(e -> {
            confirmAndDeleteBook(book);
            deleteButton.setFocusable(false); // Remove focus
            panel.requestFocusInWindow();     // Shift focus away
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel, gbc);

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JTextField createEditableField(JPanel panel, String label, GridBagConstraints gbc, int row, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        JTextField textField = new JTextField(20);
        textField.setText(value);

        if (label.equals("Book ID:")) {
            textField.setEditable(false);
            textField.setFocusable(false);
        }

        gbc.gridx = 1;
        panel.add(textField, gbc);
        return textField;
    }

    private JComboBox<String> createDropdownField(JPanel panel, String label, GridBagConstraints gbc, int row, String selectedValue) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        JComboBox<String> comboBox = new JComboBox<>(GENRES);
        comboBox.setSelectedItem(selectedValue);

        gbc.gridx = 1;
        panel.add(comboBox, gbc);
        return comboBox;
    }

    private List<Book> readBooksFromFile() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Gson gson = new Gson();
            List<Book> books = gson.fromJson(reader, new TypeToken<List<Book>>() {
            }.getType());
            return (books != null) ? books : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void writeBooksToFile(List<Book> books) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            Gson gson = new Gson();
            gson.toJson(books, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveBookDetails(Book book) {
        // Validation
        String validationMessage = validateBookDetails();
        if (!validationMessage.isEmpty()) {
            JOptionPane.showMessageDialog(this, validationMessage, "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ask for confirmation before saving
        int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to save the changes?",
                "Confirm Save",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            // Proceed with saving the book details if confirmed
            book.setBookId(Integer.parseInt(bookIdField.getText()));
            book.setIsbn(isbnField.getText());
            book.setTitle(titleField.getText());
            book.setAuthor(authorField.getText());
            book.setPublisher(publisherField.getText());
            book.setYear(Integer.parseInt(yearField.getText()));
            book.setGenre((String) genreDropdown.getSelectedItem());
            book.setCopyId(Integer.parseInt(copyIdField.getText()));
            book.setPrice(Double.parseDouble(priceField.getText()));
            book.setLanguage(languageField.getText());

            List<Book> books = readBooksFromFile();

            for (int i = 0; i < books.size(); i++) {
                if (books.get(i).getBookId() == book.getBookId()) {
                    books.set(i, book);
                    break;
                }
            }

            writeBooksToFile(books);

            // Refresh the catalog table
            homeFrame.refreshTable(catalogTable);

            JOptionPane.showMessageDialog(this, "Book details saved successfully!", "Save", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }

    private String validateBookDetails() {
        StringBuilder errorMessage = new StringBuilder();

        // Validate each field and append error messages to the errorMessage string
        if (isbnField.getText().trim().isEmpty()) {
            errorMessage.append("ISBN cannot be empty.\n");
        }
        if (titleField.getText().trim().isEmpty()) {
            errorMessage.append("Title cannot be empty.\n");
        }
        if (authorField.getText().trim().isEmpty()) {
            errorMessage.append("Author cannot be empty.\n");
        }
        if (publisherField.getText().trim().isEmpty()) {
            errorMessage.append("Publisher cannot be empty.\n");
        }
        try {
            Integer.parseInt(yearField.getText().trim());  // Validate year field
        } catch (NumberFormatException e) {
            errorMessage.append("Year must be a valid number.\n");
        }
        try {
            Double.parseDouble(priceField.getText().trim());  // Validate price field
        } catch (NumberFormatException e) {
            errorMessage.append("Price must be a valid number.\n");
        }

        // If there were no errors, return an empty string
        return errorMessage.toString();
    }

    private void confirmAndDeleteBook(Book book) {
        // Ask for confirmation before deleting
        int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this book?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            deleteBook(book);
        }
    }

    private void deleteBook(Book book) {
        List<Book> books = readBooksFromFile();
        if (books != null) {
            books.removeIf(b -> b.getBookId() == book.getBookId());
            writeBooksToFile(books);

            // Refresh the catalog table
            homeFrame.refreshTable(catalogTable);

            JOptionPane.showMessageDialog(this, "Book deleted successfully!", "Delete", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete the book. Data could not be loaded.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
