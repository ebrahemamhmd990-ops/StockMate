
package view;

import controller.ProductManager;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import model.Product;

public class MainFrame extends JFrame {

    private ProductManager manager;

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> sortCombo;

    private JTextField idField;
    private JTextField nameField;
    private JTextField categoryField;
    private JTextField quantityField;
    private JTextField priceField;

    private JLabel totalProductsLabel;
    private JLabel totalQuantityLabel;
    private JLabel totalValueLabel;
    private JLabel lowStockLabel;

    private static final int LOW_STOCK_LIMIT = 5;

    private final Color NAVY = new Color(25, 42, 70);
    private final Color BLUE = new Color(45, 105, 180);
    private final Color GREEN = new Color(39, 145, 105);
    private final Color RED = new Color(195, 65, 65);
    private final Color ORANGE = new Color(220, 145, 45);
    private final Color BACKGROUND = new Color(242, 245, 249);
    private final Color WHITE = Color.WHITE;
    private final Color TEXT = new Color(45, 55, 70);

    public MainFrame() {

        manager = new ProductManager();

        setTitle("StockMate - Inventory Management System");
        setSize(1150, 750);
        setMinimumSize(new Dimension(950, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel headerPanel = createHeader();
        JPanel dashboardPanel = createDashboard();

        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setBackground(BACKGROUND);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(dashboardPanel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        createTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 232))
        );
        scrollPane.getViewport().setBackground(WHITE);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
        tablePanel.setBackground(WHITE);

        tablePanel.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(
                    new Color(220, 225, 232)
                ),
                " Products Inventory ",
                0, 0,
                new Font("Arial", Font.BOLD, 14),
                NAVY
            )
        );

        // Sorting Panel
        JPanel sortingPanel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 8)
        );
        sortingPanel.setBackground(WHITE);

        JLabel sortLabel = new JLabel("Sort Products By:");
        sortLabel.setFont(new Font("Arial", Font.BOLD, 12));
        sortLabel.setForeground(NAVY);

        String[] sortOptions = {
            "Default Order",
            "ID - Low to High",
            "ID - High to Low",
            "Name - A to Z",
            "Name - Z to A",
            "Quantity - Low to High",
            "Quantity - High to Low",
            "Price - Low to High",
            "Price - High to Low"
        };

        sortCombo = new JComboBox<>(sortOptions);
        sortCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        sortCombo.setPreferredSize(new Dimension(190, 30));

        sortCombo.addActionListener(e -> applySorting());

        sortingPanel.add(sortLabel);
        sortingPanel.add(sortCombo);

        tablePanel.add(sortingPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        refreshTable();
    }

    // Header
    private JPanel createHeader() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(NAVY);
        panel.setBorder(new EmptyBorder(18, 22, 18, 22));

        JLabel title = new JLabel("StockMate");
        title.setFont(new Font("Arial", Font.BOLD, 27));
        title.setForeground(WHITE);

        JLabel subtitle = new JLabel(
            "Inventory Management System"
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(205, 215, 230));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(NAVY);
        textPanel.add(title);
        textPanel.add(subtitle);

        JLabel version = new JLabel("STOCK MANAGEMENT");
        version.setFont(new Font("Arial", Font.BOLD, 12));
        version.setForeground(new Color(190, 205, 225));

        panel.add(textPanel, BorderLayout.WEST);
        panel.add(version, BorderLayout.EAST);

        return panel;
    }

    // Dashboard
    private JPanel createDashboard() {

        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setBackground(BACKGROUND);

        totalProductsLabel = createValueLabel();
        totalQuantityLabel = createValueLabel();
        totalValueLabel = createValueLabel();
        lowStockLabel = createValueLabel();

        panel.add(createStatCard(
            "TOTAL PRODUCTS", totalProductsLabel, BLUE
        ));

        panel.add(createStatCard(
            "TOTAL QUANTITY", totalQuantityLabel, GREEN
        ));

        panel.add(createStatCard(
            "TOTAL STOCK VALUE", totalValueLabel, ORANGE
        ));

        panel.add(createStatCard(
            "LOW STOCK PRODUCTS", lowStockLabel, RED
        ));

        return panel;
    }

    private JLabel createValueLabel() {

        JLabel label = new JLabel("0", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 23));
        label.setForeground(NAVY);

        return label;
    }

    private JPanel createStatCard(
            String title, JLabel value, Color accent) {

        JPanel card = new JPanel(new BorderLayout(5, 12));
        card.setBackground(WHITE);

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                    0, 4, 0, 0, accent
                ),
                new EmptyBorder(15, 10, 15, 10)
            )
        );

        JLabel titleLabel = new JLabel(
            title, SwingConstants.CENTER
        );

        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(new Color(100, 110, 125));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
    }

    // Create Table
    private void createTable() {

        String[] columns = {
            "ID", "Product Name", "Category",
            "Quantity", "Price (EGP)"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);

        table.setRowHeight(32);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setBackground(WHITE);
        table.setSelectionBackground(new Color(215, 230, 250));
        table.setSelectionForeground(NAVY);
        table.setGridColor(new Color(235, 238, 243));
        table.setShowVerticalLines(false);
        table.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );
        table.setFillsViewportHeight(true);

        // Enable Sorting
        sorter = new TableRowSorter<>(tableModel);

        sorter.setComparator(0, Comparator.comparingInt(
            value -> Integer.parseInt(value.toString())
        ));

        sorter.setComparator(3, Comparator.comparingInt(
            value -> Integer.parseInt(value.toString())
        ));

        sorter.setComparator(4, Comparator.comparingDouble(
            value -> Double.parseDouble(value.toString())
        ));

        table.setRowSorter(sorter);

        // Table Header
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 38));
        header.setBackground(NAVY);
        header.setForeground(WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setReorderingAllowed(false);

        // Cell Renderer
        DefaultTableCellRenderer renderer =
            new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(
                        JTable table,
                        Object value,
                        boolean isSelected,
                        boolean hasFocus,
                        int row,
                        int column) {

                    Component component =
                        super.getTableCellRendererComponent(
                            table, value, isSelected,
                            hasFocus, row, column
                        );

                    if (!isSelected) {

                        int quantity = Integer.parseInt(
                            table.getValueAt(row, 3).toString()
                        );

                        if (quantity <= LOW_STOCK_LIMIT) {
                            component.setBackground(
                                new Color(255, 235, 235)
                            );
                        } else if (row % 2 == 0) {
                            component.setBackground(WHITE);
                        } else {
                            component.setBackground(
                                new Color(247, 249, 252)
                            );
                        }

                        component.setForeground(TEXT);

                    } else {
                        component.setBackground(
                            table.getSelectionBackground()
                        );
                        component.setForeground(
                            table.getSelectionForeground()
                        );
                    }

                    setBorder(new EmptyBorder(0, 10, 0, 10));

                    return component;
                }
            };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel()
                 .getColumn(i)
                 .setCellRenderer(renderer);
        }

        // Select Table Row
        table.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                int selectedRow = table.getSelectedRow();

                if (selectedRow != -1) {

                    int row = table.convertRowIndexToModel(
                        selectedRow
                    );

                    idField.setText(
                        tableModel.getValueAt(row, 0).toString()
                    );

                    nameField.setText(
                        tableModel.getValueAt(row, 1).toString()
                    );

                    categoryField.setText(
                        tableModel.getValueAt(row, 2).toString()
                    );

                    quantityField.setText(
                        tableModel.getValueAt(row, 3).toString()
                    );

                    priceField.setText(
                        tableModel.getValueAt(row, 4).toString()
                    );
                }
            }
        });
    }

    // Apply Sorting
    private void applySorting() {

        int selectedOption = sortCombo.getSelectedIndex();

        switch (selectedOption) {

            case 0:
                sorter.setSortKeys(null);
                break;

            case 1:
                sorter.setSortKeys(
                    java.util.Collections.singletonList(
                        new javax.swing.RowSorter.SortKey(
                            0, javax.swing.SortOrder.ASCENDING
                        )
                    )
                );
                break;

            case 2:
                sorter.setSortKeys(
                    java.util.Collections.singletonList(
                        new javax.swing.RowSorter.SortKey(
                            0, javax.swing.SortOrder.DESCENDING
                        )
                    )
                );
                break;

            case 3:
                sorter.setSortKeys(
                    java.util.Collections.singletonList(
                        new javax.swing.RowSorter.SortKey(
                            1, javax.swing.SortOrder.ASCENDING
                        )
                    )
                );
                break;

            case 4:
                sorter.setSortKeys(
                    java.util.Collections.singletonList(
                        new javax.swing.RowSorter.SortKey(
                            1, javax.swing.SortOrder.DESCENDING
                        )
                    )
                );
                break;

            case 5:
                sorter.setSortKeys(
                    java.util.Collections.singletonList(
                        new javax.swing.RowSorter.SortKey(
                            3, javax.swing.SortOrder.ASCENDING
                        )
                    )
                );
                break;

            case 6:
                sorter.setSortKeys(
                    java.util.Collections.singletonList(
                        new javax.swing.RowSorter.SortKey(
                            3, javax.swing.SortOrder.DESCENDING
                        )
                    )
                );
                break;

            case 7:
                sorter.setSortKeys(
                    java.util.Collections.singletonList(
                        new javax.swing.RowSorter.SortKey(
                            4, javax.swing.SortOrder.ASCENDING
                        )
                    )
                );
                break;

            case 8:
                sorter.setSortKeys(
                    java.util.Collections.singletonList(
                        new javax.swing.RowSorter.SortKey(
                            4, javax.swing.SortOrder.DESCENDING
                        )
                    )
                );
                break;
        }
    }

    // Bottom Panel
    private JPanel createBottomPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 12));
        panel.setBackground(BACKGROUND);

        JPanel fieldsPanel = new JPanel(
            new GridLayout(2, 5, 10, 8)
        );

        fieldsPanel.setBackground(WHITE);

        fieldsPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(
                        new Color(220, 225, 232)
                    ),
                    " Product Information ",
                    0, 0,
                    new Font("Arial", Font.BOLD, 14),
                    NAVY
                ),
                new EmptyBorder(8, 10, 10, 10)
            )
        );

        idField = createInputField();
        nameField = createInputField();
        categoryField = createInputField();
        quantityField = createInputField();
        priceField = createInputField();

        fieldsPanel.add(createFieldLabel("Product ID"));
        fieldsPanel.add(createFieldLabel("Product Name"));
        fieldsPanel.add(createFieldLabel("Category"));
        fieldsPanel.add(createFieldLabel("Quantity"));
        fieldsPanel.add(createFieldLabel("Price"));

        fieldsPanel.add(idField);
        fieldsPanel.add(nameField);
        fieldsPanel.add(categoryField);
        fieldsPanel.add(quantityField);
        fieldsPanel.add(priceField);

        panel.add(fieldsPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 10, 5)
        );

        buttonsPanel.setBackground(BACKGROUND);

        JButton addButton = createButton("Add Product", GREEN);
        JButton updateButton = createButton("Update Product", BLUE);
        JButton deleteButton = createButton("Delete Product", RED);
        JButton searchButton = createButton("Search", NAVY);
        JButton refreshButton = createButton("Refresh", ORANGE);
        JButton clearButton = createButton(
            "Clear", new Color(105, 115, 130)
        );

        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(searchButton);
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(clearButton);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        searchButton.addActionListener(e -> searchProduct());
        refreshButton.addActionListener(e -> refreshTable());
        clearButton.addActionListener(e -> clearFields());

        return panel;
    }

    private JTextField createInputField() {

        JTextField field = new JTextField();

        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(100, 30));

        return field;
    }

    private JLabel createFieldLabel(String text) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(TEXT);

        return label;
    }

    private JButton createButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        button.setPreferredSize(new Dimension(135, 36));

        return button;
    }

    // Add Product
    private void addProduct() {

        try {

            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();

            int quantity = Integer.parseInt(
                quantityField.getText().trim()
            );

            double price = Double.parseDouble(
                priceField.getText().trim()
            );

            Product product = new Product(
                id, name, category, quantity, price
            );

            if (manager.addProduct(product)) {

                JOptionPane.showMessageDialog(
                    this, "Product Added Successfully!"
                );

                refreshTable();
                clearFields();

            } else {

                JOptionPane.showMessageDialog(
                    this, "Invalid Data or Duplicate ID!"
                );
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                this, "Please Enter Valid Numbers!"
            );
        }
    }

    // Update Product
    private void updateProduct() {

        try {

            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();

            int quantity = Integer.parseInt(
                quantityField.getText().trim()
            );

            double price = Double.parseDouble(
                priceField.getText().trim()
            );

            if (manager.updateProduct(
                    id, name, category, quantity, price)) {

                JOptionPane.showMessageDialog(
                    this, "Product Updated Successfully!"
                );

                refreshTable();
                clearFields();

            } else {

                JOptionPane.showMessageDialog(
                    this, "Invalid Data or Product Not Found!"
                );
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                this, "Please Enter Valid Numbers!"
            );
        }
    }

    // Delete Product
    private void deleteProduct() {

        try {

            int id = Integer.parseInt(idField.getText().trim());

            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this product?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {

                if (manager.deleteProduct(id)) {

                    JOptionPane.showMessageDialog(
                        this, "Product Deleted!"
                    );

                    refreshTable();
                    clearFields();

                } else {

                    JOptionPane.showMessageDialog(
                        this, "Product Not Found!"
                    );
                }
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                this, "Enter a Valid Product ID!"
            );
        }
    }

    // Search Product
    private void searchProduct() {

        String keyword = idField.getText().trim();

        if (keyword.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Please Enter Product ID, Name or Category!"
            );

            return;
        }

        ArrayList<Product> results = new ArrayList<>();

        try {

            int id = Integer.parseInt(keyword);

            Product product = manager.searchProduct(id);

            if (product != null) {
                results.add(product);
            }

        } catch (NumberFormatException e) {

            results = manager.searchProducts(keyword);
        }

        tableModel.setRowCount(0);

        for (Product product : results) {

            Object[] row = {
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getQuantity(),
                product.getPrice()
            };

            tableModel.addRow(row);
        }

        if (results.isEmpty()) {

            JOptionPane.showMessageDialog(
                this, "No Products Found!"
            );

        } else if (results.size() == 1) {

            table.setRowSelectionInterval(0, 0);
        }
    }

    // Refresh Table and Dashboard
    private void refreshTable() {

        tableModel.setRowCount(0);

        int totalProducts = 0;
        long totalQuantity = 0;
        double totalStockValue = 0;
        int lowStockCount = 0;

        for (Product product : manager.getAllProducts()) {

            Object[] row = {
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getQuantity(),
                product.getPrice()
            };

            tableModel.addRow(row);

            totalProducts++;
            totalQuantity += product.getQuantity();

            totalStockValue +=
                product.getQuantity() * product.getPrice();

            if (product.getQuantity() <= LOW_STOCK_LIMIT) {
                lowStockCount++;
            }
        }

        totalProductsLabel.setText(
            String.valueOf(totalProducts)
        );

        totalQuantityLabel.setText(
            String.valueOf(totalQuantity)
        );

        NumberFormat formatter =
            NumberFormat.getNumberInstance(Locale.US);

        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        totalValueLabel.setText(
            formatter.format(totalStockValue) + " EGP"
        );

        lowStockLabel.setText(
            String.valueOf(lowStockCount)
        );

        if (lowStockCount > 0) {
            lowStockLabel.setForeground(RED);
        } else {
            lowStockLabel.setForeground(GREEN);
        }
    }

    // Clear Fields
    private void clearFields() {

        idField.setText("");
        nameField.setText("");
        categoryField.setText("");
        quantityField.setText("");
        priceField.setText("");

        table.clearSelection();
    }
}


