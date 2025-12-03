package com.mycompany.librarycatalog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.FileReader;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

public class HomeFrame {

    private static final String FILE_PATH = "src/main/java/com/mycompany/librarycatalog/data/BookData.json";
    private JTable catalogTable;  // Make catalogTable an instance variable
    private HomeFrame homeFrame;
    private AddBookFrame addBookFrame;  // Track the AddBookFrame instance

    public void BookDetailsFrame(HomeFrame homeFrame, JTable catalogTable, Book book) {
        this.homeFrame = homeFrame;  // Use the passed homeFrame correctly
    }

    public void showHomeFrame() {
        JFrame frame = createMainFrame();
        JPanel topPanel = createTopPanel(frame);
        frame.add(topPanel, BorderLayout.NORTH);

        catalogTable = createCatalogTable(frame);  // Use the instance variable directly
        JScrollPane scrollPane = new JScrollPane(catalogTable);
        scrollPane.setPreferredSize(new Dimension(frame.getWidth(), 300));
        frame.add(scrollPane, BorderLayout.CENTER);

        addDynamicColumnResizing(catalogTable, frame);
        addAddBookButton(frame);

        // Ensure no focus on any text field after the window opens
        frame.setFocusable(true);  // Make sure the frame itself can receive focus
        frame.requestFocusInWindow(); // Remove focus from any components
        frame.setVisible(true);
    }

    private JFrame createMainFrame() {
        JFrame frame = new JFrame("Library Catalog");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        return frame;
    }

    private JPanel createTopPanel(JFrame frame) {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.DARK_GRAY);
        topPanel.setPreferredSize(new Dimension(frame.getWidth(), 120));

        JLabel titleLabel = createTitleLabel();
        JPanel rightPanel = createRightPanel(frame);
        JPanel searchPanel = createSearchPanel(frame);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        return topPanel;
    }

    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel("LIBRARY CATALOG");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        return titleLabel;
    }

    private JPanel createRightPanel(JFrame frame) {
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel helpLabel = createLinkLabel("HELP", new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showHelpDialog(frame);
            }
        });
        JLabel aboutUsLabel = createLinkLabel("ABOUT US", new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showAboutUsDialog(frame);
            }
        });

        rightPanel.add(helpLabel);
        rightPanel.add(aboutUsLabel);
        return rightPanel;
    }

    private JLabel createLinkLabel(String text, MouseAdapter mouseListener) {
        JLabel label = new JLabel("<html><u>" + text + "</u></html>");
        label.setForeground(Color.LIGHT_GRAY);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add mouse listener to change color on hover
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setForeground(Color.WHITE); // Change color on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setForeground(Color.LIGHT_GRAY); // Reset color when not hovered
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                mouseListener.mouseClicked(e); // Call the original mouse click event
            }
        });

        return label;
    }

    private JPanel createSearchPanel(JFrame frame) {
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 10, 0, 10);

        JTextField searchField = createSearchField();
        JButton searchButton = createSearchButton(searchField);

        searchPanel.add(searchField, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.0;
        searchPanel.add(searchButton, gbc);

        return searchPanel;
    }

    private JTextField createSearchField() {
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setText("Search books...");
        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search books...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search books...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        return searchField;
    }

    private JButton createSearchButton(JTextField searchField) {
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBackground(Color.DARK_GRAY);

        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();  // Get the query and trim whitespace

            // Check if the button text is "Refresh"
            if (searchButton.getText().equals("Refresh")) {
                // Reset the search field and table filter regardless of its current content
                searchField.setText("Search books...");  // Reset the search field
                searchField.setForeground(Color.GRAY);  // Set placeholder color back
                filterTable(""); // Clear the filter to show all rows
                searchButton.setText("Search"); // Change button text back to "Search"
            } else {
                // Apply search if the button is in "Search" mode
                if (query.equals("Search books...") || query.isEmpty()) {
                    filterTable(""); // Reset sorting
                    searchButton.setText("Search"); // Keep button text as "Search"
                } else {
                    filterTable(query.toLowerCase()); // Apply search filter
                    // If no rows match, change the button text to "Refresh"
                    if (catalogTable.getRowCount() == 0) {
                        searchButton.setText("Refresh");
                    } else {
                        searchButton.setText("Search");
                    }
                }
            }
        });

        return searchButton;
    }

    private void filterTable(String query) {
        DefaultTableModel tableModel = (DefaultTableModel) catalogTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        catalogTable.setRowSorter(sorter);

        if (query.isEmpty()) {
            sorter.setRowFilter(null);  // ✅ Reset filter when search is empty
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));  // Case-insensitive search
        }
    }

    private Book getBookFromTable(JTable table, int row) {
        int bookId = Integer.parseInt(table.getValueAt(row, 0).toString());
        String isbn = table.getValueAt(row, 1).toString();
        String title = table.getValueAt(row, 2).toString();
        String author = table.getValueAt(row, 3).toString();
        String publisher = table.getValueAt(row, 4).toString();
        int year = Integer.parseInt(table.getValueAt(row, 5).toString());
        String genre = table.getValueAt(row, 6).toString();
        int copyId = Integer.parseInt(table.getValueAt(row, 7).toString());
        double price = Double.parseDouble(table.getValueAt(row, 8).toString());
        String language = table.getValueAt(row, 9).toString();

        return new Book(bookId, isbn, title, author, publisher, year, genre, copyId, price, language);
    }

    private JTable createCatalogTable(JFrame frame) {
        DefaultTableModel tableModel = createTableModel();
        JTable catalogTable = new JTable(tableModel);
        catalogTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Enable row selection mode
        catalogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // This ensures only one row is selected at a time
        catalogTable.setColumnSelectionAllowed(false);  // Ensure only row selection is allowed
        catalogTable.setRowSelectionAllowed(true);  // Enable row selection

        // Disable editing for all cells
        catalogTable.setDefaultEditor(Object.class, null);

        // Align some columns to center
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        catalogTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Book ID
        catalogTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // ISBN
        catalogTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Year
        catalogTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Copy ID
        catalogTable.getColumnModel().getColumn(8).setCellRenderer(centerRenderer); // Price

        // Add double-click listener
        catalogTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {  // Double-click detected
                    int selectedRow = catalogTable.getSelectedRow();

                    if (selectedRow != -1) {
                        // Get the book from the table row
                        Book selectedBook = getBookFromTable(catalogTable, selectedRow);

                        // Create and show the BookDetailsFrame
                        new BookDetailsFrame(HomeFrame.this, catalogTable, selectedBook);
                    }
                }
            }
        });

        // Set tooltip for the table
        catalogTable.setToolTipText("Double-click a row to edit the book details");

        // Add hover effect on rows to highlight entire row (all columns)
        catalogTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = catalogTable.rowAtPoint(e.getPoint());
                if (row > -1) {
                    // Ensure the entire row is selected across all columns
                    catalogTable.setRowSelectionInterval(row, row);
                    // Optionally, clear column selection (if needed)
                    catalogTable.clearSelection();
                }
            }
        });

        // Add row hover effect
        catalogTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = catalogTable.rowAtPoint(e.getPoint());
                if (row > -1) {
                    catalogTable.setRowSelectionInterval(row, row); // Select full row
                } else {
                    catalogTable.clearSelection(); // Clear selection if not hovering
                }
            }
        });

        return catalogTable;
    }

    private DefaultTableModel createTableModel() {
        String[] columnNames = {"ID", "ISBN", "Title", "Author", "Publisher", "Year", "Genre", "Copy ID", "Price", "Language"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        try (FileReader reader = new FileReader(FILE_PATH)) {
            List<Book> books = new Gson().fromJson(reader, new TypeToken<List<Book>>() {
            }.getType());
            for (Book book : books) {
                tableModel.addRow(new Object[]{
                    book.getBookId(), // Add Book ID
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getYear(),
                    book.getGenre(),
                    book.getCopyId(),
                    String.format("%.2f", book.getPrice()), // Format price to 2 decimal places
                    book.getLanguage()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading book data. Please check the file.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return tableModel;
    }

    void refreshTable(JTable catalogTable) {
        // Create a new table model and set it to the table
        DefaultTableModel tableModel = createTableModel();
        catalogTable.setModel(tableModel);

        // Reapply custom column alignments (if needed)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        catalogTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Book ID
        catalogTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // ISBN
        catalogTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Year
        catalogTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Copy ID
        catalogTable.getColumnModel().getColumn(8).setCellRenderer(centerRenderer); // Price

        // Reapply column resizing logic
        addDynamicColumnResizing(catalogTable, (JFrame) SwingUtilities.getWindowAncestor(catalogTable));

        // Reapply column width format
        int tableWidth = catalogTable.getWidth();
        catalogTable.getColumnModel().getColumn(0).setPreferredWidth((int) (tableWidth * 0.06)); // Book ID
        catalogTable.getColumnModel().getColumn(1).setPreferredWidth((int) (tableWidth * 0.15)); // ISBN
        catalogTable.getColumnModel().getColumn(2).setPreferredWidth((int) (tableWidth * 0.18)); // Title
        catalogTable.getColumnModel().getColumn(3).setPreferredWidth((int) (tableWidth * 0.15)); // Author
        catalogTable.getColumnModel().getColumn(4).setPreferredWidth((int) (tableWidth * 0.15)); // Publisher
        catalogTable.getColumnModel().getColumn(5).setPreferredWidth((int) (tableWidth * 0.07)); // Year
        catalogTable.getColumnModel().getColumn(6).setPreferredWidth((int) (tableWidth * 0.10)); // Genre
        catalogTable.getColumnModel().getColumn(7).setPreferredWidth((int) (tableWidth * 0.07)); // Copy ID
        catalogTable.getColumnModel().getColumn(8).setPreferredWidth((int) (tableWidth * 0.08)); // Price
        catalogTable.getColumnModel().getColumn(9).setPreferredWidth((int) (tableWidth * 0.10)); // Language
    }

    private void addDynamicColumnResizing(JTable catalogTable, JFrame frame) {
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                int tableWidth = catalogTable.getWidth();
                catalogTable.getColumnModel().getColumn(0).setPreferredWidth((int) (tableWidth * 0.06)); // Book ID
                catalogTable.getColumnModel().getColumn(1).setPreferredWidth((int) (tableWidth * 0.15)); // ISBN
                catalogTable.getColumnModel().getColumn(2).setPreferredWidth((int) (tableWidth * 0.18)); // Title
                catalogTable.getColumnModel().getColumn(3).setPreferredWidth((int) (tableWidth * 0.15)); // Author
                catalogTable.getColumnModel().getColumn(4).setPreferredWidth((int) (tableWidth * 0.15)); // Publisher
                catalogTable.getColumnModel().getColumn(5).setPreferredWidth((int) (tableWidth * 0.07)); // Year
                catalogTable.getColumnModel().getColumn(6).setPreferredWidth((int) (tableWidth * 0.10)); // Genre
                catalogTable.getColumnModel().getColumn(7).setPreferredWidth((int) (tableWidth * 0.07)); // Copy ID
                catalogTable.getColumnModel().getColumn(8).setPreferredWidth((int) (tableWidth * 0.08)); // Price
                catalogTable.getColumnModel().getColumn(9).setPreferredWidth((int) (tableWidth * 0.10)); // Language
            }
        });
    }

    private void addAddBookButton(JFrame frame) {
        JButton addBookButton = new JButton("Add Book");
        addBookButton.setFont(new Font("Arial", Font.BOLD, 14));
        addBookButton.setBackground(Color.DARK_GRAY);
        addBookButton.setForeground(Color.WHITE);

        addBookButton.addActionListener(e -> {
            // Only allow opening AddBookFrame if it is not already open
            if (addBookFrame == null || !addBookFrame.isDisplayable()) {
                addBookFrame = new AddBookFrame(this, catalogTable);  // Create a new AddBookFrame
                addBookFrame.setVisible(true);  // Make the frame visible
            } else {
                // Optionally bring the existing AddBookFrame to the front
                addBookFrame.toFront();
                addBookFrame.requestFocus();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addBookButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void showHelpDialog(JFrame frame) {
        JOptionPane.showMessageDialog(frame,
                "For help, contact support@library.com or visit our website.",
                "Help",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAboutUsDialog(JFrame frame) {
        JOptionPane.showMessageDialog(frame,
                "Library Catalog v1.0\nDeveloped by XYZ Team\nFor more info, visit: www.librarycatalog.com",
                "About Us",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
