package ims.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ims.database.DatabaseConnection;
import ims.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ManageStockController {
    
    @FXML private ComboBox<Product> productComboBox;
    @FXML private TextField quantityField;
    @FXML private ComboBox<String> actionComboBox;
    @FXML private TextArea reasonField;
    @FXML private Label currentStockLabel;
    @FXML private Label statusLabel;
    
    // New fields for selected product info
    @FXML private VBox selectedProductInfo;
    @FXML private Label selectedProductName;
    @FXML private Label selectedProductSku;
    @FXML private Label selectedProductCategory;
    @FXML private Label selectedProductStock;

    private DataController dataController;
    private ObservableList<Product> productsList;

    @FXML
    public void initialize() {
        this.dataController = DataController.getInstance();
        this.productsList = FXCollections.observableArrayList();
        
        setupForm();
        loadProducts();
    }

    private void setupForm() {
        // Populate combo boxes
        productComboBox.setItems(FXCollections.observableArrayList(dataController.getProducts()));
        actionComboBox.setItems(FXCollections.observableArrayList("Add Stock", "Remove Stock"));
        
        // Set up cell value factories for product combo box
        productComboBox.setCellFactory(param -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setText(null);
                } else {
                    setText(product.getName() + " (SKU: " + product.getSKU() + ")");
                }
            }
        });

        productComboBox.setButtonCell(new ListCell<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setText("Select Product");
                } else {
                    setText(product.getName() + " (SKU: " + product.getSKU() + ")");
                }
            }
        });

        // Event handler for product selection
        productComboBox.setOnAction(event -> updateSelectedProductInfo());
    }

    private void loadProducts() {
        productsList.clear();
        productsList.addAll(dataController.getProducts());
    }

    private void updateSelectedProductInfo() {
        Product selectedProduct = productComboBox.getValue();
        if (selectedProduct != null) {
            // Update current stock label
            currentStockLabel.setText("Current Stock: " + selectedProduct.getQuantity());
            
            // Update detailed product info
            selectedProductName.setText(selectedProduct.getName());
            selectedProductSku.setText(selectedProduct.getSKU());
            selectedProductCategory.setText(selectedProduct.getCategory() != null ? 
                selectedProduct.getCategory().getName() : "No Category");
            selectedProductStock.setText(String.valueOf(selectedProduct.getQuantity()));
            
            // Show the product info section
            selectedProductInfo.setVisible(true);
        } else {
            currentStockLabel.setText("Current Stock: 0");
            selectedProductInfo.setVisible(false);
        }
    }

    @FXML
    private void updateStock() {
        Product product = productComboBox.getValue();
        String action = actionComboBox.getValue();
        String quantityText = quantityField.getText();
        String reason = reasonField.getText();

        // 1. Validate
        if (product == null || action == null || quantityText.isEmpty() || reason.isEmpty()) {
            showStatus("Please fill all required fields", "error");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(quantityText);
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showStatus("Enter a valid quantity", "error");
            return;
        }

        try {
            // 2. Update DB
            int changeQty = action.equals("Add Stock") ? qty : -qty;

            if (changeQty < 0 && product.getQuantity() < qty) {
                showStatus("Not enough stock. Current: " + product.getQuantity(), "error");
                return;
            }

            dataController.updateProductQuantity(product, changeQty);

            // 3. Log inventory change
            int currentUserId = 1; // TODO: get current logged-in user
            dataController.logInventoryChange(currentUserId, product.getProductId(), action, qty, reason);
            
            // 4. Update Java object & refresh display
            if (changeQty > 0) {
                product.addQuantity(qty);
            } else {
                product.removeQuantity(qty);
            }

            // Refresh the displayed information
            updateSelectedProductInfo();
            clearForm();
            showStatus("Stock updated successfully", "success");

        } catch (Exception e) {
            showStatus("Error updating stock: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    private void clearForm() {
        quantityField.clear();
        actionComboBox.setValue(null);
        reasonField.clear();
        statusLabel.setVisible(false);
        // Don't clear the product selection so user can make multiple adjustments
    }

    private void showStatus(String message, String type) {
        statusLabel.setText(message);
        statusLabel.setVisible(true);
        
        switch (type) {
            case "success":
                statusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                break;
            case "error":
                statusLabel.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                break;
        }
    }
}