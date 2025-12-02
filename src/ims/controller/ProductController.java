package ims.controller;

import ims.model.Product;
import ims.model.Category;
import ims.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class ProductController {

    @FXML private TextField searchField;

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colSKU;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, String> colCategory;

    @FXML private TextField addNameField;
    @FXML private TextField addSKUField;
    @FXML private TextField addPriceField;
    @FXML private TextField addCategoryField;

    private ObservableList<Product> products = FXCollections.observableArrayList();
    private ObservableList<Product> allProducts = FXCollections.observableArrayList();
    
    // Reference to DataController
    private DataController dataController;

    @FXML
    public void initialize() {
        // Get DataController instance
        dataController = DataController.getInstance();
        
        // Map columns to Product properties
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getProductId()));
        colName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        colSKU.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getSKU()));
        colPrice.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getPrice()));
        
        // Extract category name instead of Category object
        colCategory.setCellValueFactory(c -> {
            Category category = c.getValue().getCategory();
            String categoryName = (category != null) ? category.getName() : "No Category";
            return new javafx.beans.property.SimpleStringProperty(categoryName);
        });

        // Load products from DataController
        loadProductsFromDataController();
        productTable.setItems(products);
    }

    /**
     * Load products from DataController (already cached in memory)
     */
    private void loadProductsFromDataController() {
        products.clear();
        allProducts.clear();
        
        // Get products from DataController
        products.addAll(dataController.getProducts());
        allProducts.addAll(dataController.getProducts());
        
        System.out.println("✅ Loaded " + products.size() + " products from DataController");
    }

    /**
     * Refresh products from DataController
     */
    private void refreshProducts() {
        // Reload all data in DataController to sync with database
        dataController.loadAllData();
        
        // Reload products into table
        loadProductsFromDataController();
        productTable.refresh();
    }

    /**
     * Add new product to database
     */
    @FXML
    public void addProduct() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            String name = addNameField.getText().trim();
            String sku = addSKUField.getText().trim();
            double price = Double.parseDouble(addPriceField.getText().trim());
            String categoryName = addCategoryField.getText().trim();

            conn = DatabaseConnection.getConnection();

            // Step 1: Get or create category
            int categoryId = getOrCreateCategory(conn, categoryName);

            // Step 2: Insert product (quantity defaults to 0)
            String sql = "INSERT INTO products (product_name, sku, price, category_id) " +
                        "VALUES (?, ?, ?, ?)";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, sku);
            stmt.setDouble(3, price);
            stmt.setInt(4, categoryId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Log user activity
                if (dataController.getCurrentUser() != null) {
                    dataController.logUserActivity(
                        dataController.getCurrentUser().getUserId(),
                        "Added new product: " + name
                    );
                }
                
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                         "Product added successfully!");
                clearAddFields();
                
                // Refresh DataController and table
                refreshProducts();
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid number for price!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add product: " + e.getMessage());
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    /**
     * Get existing category or create new one
     */
    private int getOrCreateCategory(Connection conn, String categoryName) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Check if category exists
            String selectSql = "SELECT category_id FROM categories WHERE category_name = ?";
            stmt = conn.prepareStatement(selectSql);
            stmt.setString(1, categoryName);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("category_id");
            }

            // Category doesn't exist, create it
            rs.close();
            stmt.close();

            // Get next category_id
            String maxIdSql = "SELECT ISNULL(MAX(category_id), 0) + 1 AS next_id FROM categories";
            stmt = conn.prepareStatement(maxIdSql);
            rs = stmt.executeQuery();
            
            int nextId = 1;
            if (rs.next()) {
                nextId = rs.getInt("next_id");
            }
            
            rs.close();
            stmt.close();

            // Insert new category with explicit ID
            String insertSql = "INSERT INTO categories (category_id, category_name) VALUES (?, ?)";
            stmt = conn.prepareStatement(insertSql);
            stmt.setInt(1, nextId);
            stmt.setString(2, categoryName);
            stmt.executeUpdate();

            return nextId;

        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
    }

    /**
     * Update existing product in database
     */
    @FXML
    public void updateProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a product to update!");
            return;
        }

        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            String name = addNameField.getText().trim();
            String sku = addSKUField.getText().trim();
            double price = Double.parseDouble(addPriceField.getText().trim());
            String categoryName = addCategoryField.getText().trim();

            conn = DatabaseConnection.getConnection();

            // Get or create category
            int categoryId = getOrCreateCategory(conn, categoryName);

            // Update product (quantity is NOT updated here)
            String sql = "UPDATE products SET product_name = ?, sku = ?, price = ?, category_id = ? " +
                        "WHERE product_id = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, sku);
            stmt.setDouble(3, price);
            stmt.setInt(4, categoryId);
            stmt.setInt(5, selected.getProductId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Log user activity
                if (dataController.getCurrentUser() != null) {
                    dataController.logUserActivity(
                        dataController.getCurrentUser().getUserId(),
                        "Updated product: " + name + " (ID: " + selected.getProductId() + ")"
                    );
                }
                
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product updated successfully!");
                clearAddFields();
                
                // Refresh DataController and table
                refreshProducts();
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid number for price!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update product: " + e.getMessage());
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    /**
     * Delete selected product from database
     */
    @FXML
    public void deleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a product to delete!");
            return;
        }

        // Confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Product");
        confirmAlert.setContentText("Are you sure you want to delete: " + selected.getName() + "?");

        if (confirmAlert.showAndWait().get() != ButtonType.OK) {
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM products WHERE product_id = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, selected.getProductId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Log user activity
                if (dataController.getCurrentUser() != null) {
                    dataController.logUserActivity(
                        dataController.getCurrentUser().getUserId(),
                        "Deleted product: " + selected.getName() + " (ID: " + selected.getProductId() + ")"
                    );
                }
                
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product deleted successfully!");
                
                // Refresh DataController and table
                refreshProducts();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete product: " + e.getMessage());
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    /**
     * Search products by name, SKU, or category
     */
    @FXML
    public void searchProduct() {
        String keyword = searchField.getText().toLowerCase().trim();
        
        if (keyword.isEmpty()) {
            productTable.setItems(allProducts);
            return;
        }

        ObservableList<Product> filtered = FXCollections.observableArrayList();
        
        for (Product p : allProducts) {
            boolean matchesName = p.getName().toLowerCase().contains(keyword);
            boolean matchesSKU = p.getSKU().toLowerCase().contains(keyword);
            boolean matchesCategory = (p.getCategory() != null) && 
                                     p.getCategory().getName().toLowerCase().contains(keyword);

            if (matchesName || matchesSKU || matchesCategory) {
                filtered.add(p);
            }
        }

        productTable.setItems(filtered);
    }

    /**
     * Load selected product data into form fields for editing
     */
    @FXML
    public void loadSelectedProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        
        if (selected != null) {
            addNameField.setText(selected.getName());
            addSKUField.setText(selected.getSKU());
            addPriceField.setText(String.valueOf(selected.getPrice()));
            if (selected.getCategory() != null) {
                addCategoryField.setText(selected.getCategory().getName());
            }
        }
    }

    /**
     * Validate input fields
     */
    private boolean validateInputs() {
        if (addNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Product name is required!");
            return false;
        }

        if (addSKUField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "SKU is required!");
            return false;
        }

        if (addPriceField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Price is required!");
            return false;
        }

        if (addCategoryField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Category is required!");
            return false;
        }

        try {
            double price = Double.parseDouble(addPriceField.getText().trim());
            if (price < 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Price must be positive!");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Price must be a valid number!");
            return false;
        }

        return true;
    }

    /**
     * Clear all input fields
     */
    @FXML
    public void clearAddFields() {
        addNameField.clear();
        addSKUField.clear();
        addPriceField.clear();
        addCategoryField.clear();
    }

    /**
     * Show alert dialog
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Close database resources safely
     */
    private void closeResources(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}