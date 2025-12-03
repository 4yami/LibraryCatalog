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
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.RowFilter;
import javax.swing.event.HyperlinkEvent;
import javax.swing.table.TableRowSorter;

public class HomeFrame {

    private static final String FILE_PATH = "src/main/java/com/mycompany/librarycatalog/data/BookData.json";
    private JTable catalogTable;  // Table to display catalog of books
    private HomeFrame homeFrame;
    private AddBookFrame addBookFrame;  // Track instance of AddBookFrame to avoid duplicates

    // Constructor for BookDetailsFrame, which opens details of a selected book
    public void BookDetailsFrame(HomeFrame homeFrame, JTable catalogTable, Book book) {
        this.homeFrame = homeFrame;  // Assign passed homeFrame to the instance variable
    }

    // Method to initialize and display the main HomeFrame window
    public void showHomeFrame() {
        JFrame frame = createMainFrame();  // Create main frame for the application
        JPanel topPanel = createTopPanel(frame);  // Create and set up the top section of the UI
        frame.add(topPanel, BorderLayout.NORTH); // Add top panel to the frame

        catalogTable = createCatalogTable(frame);  // Create table to display books
        JScrollPane scrollPane = new JScrollPane(catalogTable);
        scrollPane.setPreferredSize(new Dimension(frame.getWidth(), 300));  // Set scrollable area size
        frame.add(scrollPane, BorderLayout.CENTER);  // Add table to the center of the frame

        addDynamicColumnResizing(catalogTable, frame);  // Allow columns to resize dynamically based on frame size
        addAddBookButton(frame);  // Add button to add a new book

        frame.setFocusable(true);  // Make sure the frame can receive focus
        frame.requestFocusInWindow(); // Ensure no component has focus initially
        frame.setVisible(true);  // Make the frame visible
    }

    // Method to create the main JFrame with specific settings
    private JFrame createMainFrame() {
        JFrame frame = new JFrame("Library Catalog");
        frame.setSize(800, 600);  // Set window size
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Ensure the application closes when the window is closed
        frame.setLayout(new BorderLayout());  // Set layout for the frame
        frame.setLocationRelativeTo(null);  // Center the frame on the screen
        return frame;
    }

    // Method to create and set up the top panel with title and action buttons
    private JPanel createTopPanel(JFrame frame) {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.DARK_GRAY);  // Set background color for the panel
        topPanel.setPreferredSize(new Dimension(frame.getWidth(), 120));  // Set the preferred height of the top panel

        JLabel titleLabel = createTitleLabel();  // Create title label
        JPanel rightPanel = createRightPanel(frame);  // Create panel for right-side buttons
        JPanel searchPanel = createSearchPanel(frame);  // Create search panel

        topPanel.add(titleLabel, BorderLayout.WEST);  // Add title to the left of the panel
        topPanel.add(rightPanel, BorderLayout.EAST);  // Add right-side panel for help/about buttons
        topPanel.add(searchPanel, BorderLayout.SOUTH);  // Add search panel to the bottom of the top panel

        return topPanel;
    }

    // Method to create the title label with specific font and styling
    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel("LIBRARY CATALOG");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 28));  // Set font style for the title
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));  // Add padding to the title
        return titleLabel;
    }

    // Method to create the right panel with help and about links
    private JPanel createRightPanel(JFrame frame) {
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));  // Panel for right-aligned buttons
        rightPanel.setOpaque(false);  // Set panel to be transparent
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));  // Set padding for the panel

        JLabel helpLabel = createLinkLabel("HELP", new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showHelpDialog(frame);  // Open help dialog when clicked
            }
        });
        JLabel aboutUsLabel = createLinkLabel("ABOUT US", new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showAboutUsDialog(frame);  // Open about us dialog when clicked
            }
        });

        rightPanel.add(helpLabel);  // Add help link
        rightPanel.add(aboutUsLabel);  // Add about us link
        return rightPanel;
    }

    // Helper method to create a clickable link with mouse listener
    private JLabel createLinkLabel(String text, MouseAdapter mouseListener) {
        JLabel label = new JLabel("<html><u>" + text + "</u></html>");  // HTML to underline the text
        label.setForeground(Color.LIGHT_GRAY);  // Set initial text color
        label.setFont(new Font("Arial", Font.PLAIN, 12));  // Set font style
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Set cursor to hand on hover

        // Add mouse listener to change the color when hovering
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setForeground(Color.WHITE);  // Change text color when hovered
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setForeground(Color.LIGHT_GRAY);  // Reset text color when mouse exits
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                mouseListener.mouseClicked(e);  // Trigger the provided mouse listener action
            }
        });

        return label;
    }

    // Method to create the search panel with search field and buttons
    private JPanel createSearchPanel(JFrame frame) {
        JPanel searchPanel = new JPanel(new GridBagLayout());  // Panel to arrange components using GridBagLayout
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));  // Set padding around the panel

        GridBagConstraints gbc = new GridBagConstraints();  // Constraints for positioning components in GridBagLayout
        gbc.fill = GridBagConstraints.HORIZONTAL;  // Allow components to fill the horizontal space
        gbc.gridx = 0;  // Set initial column position for search field
        gbc.weightx = 1.0;  // Give the search field the full available width
        gbc.insets = new Insets(0, 10, 0, 10);  // Add padding around the components

        JTextField searchField = createSearchField();  // Create the search field
        JButton searchButton = createSearchButton(searchField);  // Create the search button

        gbc.gridx = 0;
        searchPanel.add(searchField, gbc);  // Add search field to the panel

        // Create and add refresh button beside the search button
        JButton refreshButton = createRefreshButton(searchField, searchButton);
        gbc.gridx = 1;  // Position refresh button beside search button
        gbc.weightx = 0.0;  // Reset weightx for the refresh button
        searchPanel.add(searchButton, gbc);  // Add search button

        gbc.gridx = 2;  // Position refresh button next to search button
        searchPanel.add(refreshButton, gbc);  // Add refresh button

        return searchPanel;
    }

    // Method to create the search input field with placeholder and behavior on focus events
    private JTextField createSearchField() {
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setText("Search books...");
        searchField.setForeground(Color.GRAY);  // Set placeholder text color

        // Handle focus events for clearing placeholder text when clicked
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search books...")) {
                    searchField.setText("");  // Clear the placeholder when clicked
                    searchField.setForeground(Color.BLACK);  // Change text color to black
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search books...");  // Restore placeholder when focus is lost
                    searchField.setForeground(Color.GRAY);  // Set text color to gray again
                }
            }
        });
        return searchField;
    }

    // Method to create the search button with action logic
    private JButton createSearchButton(JTextField searchField) {
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBackground(Color.DARK_GRAY);

        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();  // Get the query and trim whitespace

            if (searchButton.getText().equals("Refresh")) {
                // Reset search field and table filter
                searchField.setText("Search books...");
                searchField.setForeground(Color.GRAY);
                filterTable("");  // Clear any filters applied to the table
                searchButton.setText("Search");  // Change button text back to "Search"
            } else {
                // Apply search if the button is in "Search" mode
                if (query.equals("Search books...") || query.isEmpty()) {
                    filterTable("");  // Reset table filter
                    searchButton.setText("Search");
                } else {
                    filterTable(query.toLowerCase());  // Apply search filter
                    if (catalogTable.getRowCount() == 0) {
                        searchButton.setText("Refresh");  // If no results, change to "Refresh"
                    } else {
                        searchButton.setText("Search");  // Reset to "Search" if there are results
                    }
                }
            }
        });

        return searchButton;
    }

    // Method to create the refresh button to reset the search
    private JButton createRefreshButton(JTextField searchField, JButton searchButton) {
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBackground(Color.DARK_GRAY);

        refreshButton.addActionListener(e -> {
            // Reset search field and table filter
            searchField.setText("Search books...");
            searchField.setForeground(Color.GRAY);
            filterTable("");  // Clear the table filter
            searchButton.setText("Search");  // Change the search button text back to "Search"
        });

        return refreshButton;
    }

    // Method to apply the search filter to the table based on user input
    private void filterTable(String query) {
        DefaultTableModel tableModel = (DefaultTableModel) catalogTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        catalogTable.setRowSorter(sorter);

        if (query.isEmpty()) {
            sorter.setRowFilter(null);  // Remove filter when query is empty
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));  // Case-insensitive search filter
        }
    }

//    questionable need repair error
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
        catalogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Single row selection
        catalogTable.setColumnSelectionAllowed(false);
        catalogTable.setRowSelectionAllowed(true);

        // Disable editing for all cells
        catalogTable.setDefaultEditor(Object.class, null);

        // Apply column alignment settings
        alignTableColumns(catalogTable);

        // Add double-click listener
        catalogTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {  // Double-click detected
                    int selectedRow = catalogTable.getSelectedRow();
                    if (selectedRow != -1) {
                        Book selectedBook = getBookFromTable(catalogTable, selectedRow);
                        new BookDetailsFrame(HomeFrame.this, catalogTable, selectedBook);
                    }
                }
            }
        });

        // Set tooltip for the table
        catalogTable.setToolTipText("Double-click a row to edit the book details");

        // Hover effect for row highlighting
        catalogTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = catalogTable.rowAtPoint(e.getPoint());
                if (row > -1) {
                    catalogTable.setRowSelectionInterval(row, row);
                }
            }
        });

        return catalogTable;
    }

    private DefaultTableModel createTableModel() {
        String[] columnNames = {"ID", "ISBN", "Title", "Author", "Publisher", "Year", "Genre", "Copy ID", "Price(RM)", "Language"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        try (FileReader reader = new FileReader(FILE_PATH)) {
            List<Book> books = new Gson().fromJson(reader, new TypeToken<List<Book>>() {
            }.getType());
            for (Book book : books) {
                tableModel.addRow(new Object[]{
                    book.getBookId(), book.getIsbn(), book.getTitle(), book.getAuthor(),
                    book.getPublisher(), book.getYear(), book.getGenre(), book.getCopyId(),
                    String.format("%.2f", book.getPrice()), book.getLanguage()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading book data. Please check the file.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return tableModel;
    }

    public void refreshTable(JTable catalogTable) {
        // Read the list of books from the file
        List<Book> books = loadBooksFromFile();

        // Get the current table model
        DefaultTableModel model = (DefaultTableModel) catalogTable.getModel();

        // Clear the table before updating
        model.setRowCount(0);

        // Add rows to the table model based on the updated list of books
        for (Book book : books) {
            model.addRow(new Object[]{
                book.getBookId(), book.getIsbn(), book.getTitle(), book.getAuthor(),
                book.getPublisher(), book.getYear(), book.getGenre(), book.getCopyId(),
                String.format("%.2f", book.getPrice()), book.getLanguage()
            });
        }

        // Ensure column alignment is reapplied after refresh
        alignTableColumns(catalogTable);

        // Refresh the table to reflect the changes
        catalogTable.revalidate();
        catalogTable.repaint();
    }

// Method to align specific table columns (to avoid redundancy)
    private void alignTableColumns(JTable catalogTable) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        int[] centerColumns = {0, 1, 5, 7, 8}; // Columns that need to be centered
        for (int col : centerColumns) {
            catalogTable.getColumnModel().getColumn(col).setCellRenderer(centerRenderer);
        }
    }

    private List<Book> loadBooksFromFile() {
        List<Book> books = new ArrayList<>();
        try (FileReader reader = new FileReader(FILE_PATH)) {
            books = new Gson().fromJson(reader, new TypeToken<List<Book>>() {
            }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
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
                catalogTable.getColumnModel().getColumn(8).setPreferredWidth((int) (tableWidth * 0.10)); // Price
                catalogTable.getColumnModel().getColumn(9).setPreferredWidth((int) (tableWidth * 0.08)); // Language
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
        String message = "<html>"
                + "<p>Need assistance?</p>"
                + "<p>📧 Email us at <a href='mailto:support@library.com'>support@library.com</a></p>"
                + "<p>🌐 Visit our <a href='https://www.librarycatalog.com'>website</a> for FAQs and resources.</p>"
                + "</html>";

        showHtmlDialog(frame, "We're Here to Help!", message);
    }

    private void showAboutUsDialog(JFrame frame) {
        String message = "<html>"
                + "<p>Library Catalog System v1.0</p>"
                + "<p>Designed to simplify library management and improve user experience</p>"
                + "<p>📞 Support: <a href='mailto:support@librarycatalog.com'>support@librarycatalog.com</a></p>"
                + "<p>🌐 Website: <a href='https://www.librarycatalog.com'>www.librarycatalog.com</a></p>"
                + "<p>Thank you for using our software!</p>"
                + "</html>";

        showHtmlDialog(frame, "About Us", message);
    }

    private void showHtmlDialog(JFrame frame, String title, String htmlMessage) {
        JEditorPane pane = new JEditorPane("text/html",
                "<html><body style='font-family: Arial, sans-serif; font-size: 11px;'>"
                + htmlMessage + "</body></html>");
        pane.setEditable(false);
        pane.setOpaque(false);

        // Add hyperlink listener to open links in browser
        pane.addHyperlinkListener(e -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JOptionPane.showMessageDialog(frame, pane, title, JOptionPane.INFORMATION_MESSAGE);
    }

}
