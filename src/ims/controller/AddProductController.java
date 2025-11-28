package ims.controller;

import ims.model.Product;
import ims.model.ProductData;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

public class AddProductController {

    @FXML private TextField nameField;
    @FXML private TextField skuField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> supplierComboBox;
    @FXML private TextArea descriptionField;
    @FXML private Label messageLabel;

    private ProductData productData;

    @FXML
    public void initialize() {
        productData = ProductData.getInstance();
        
        // Set up category dropdown
        ObservableList<String> categories = FXCollections.observableArrayList(
            "Electronics", "Clothing", "Food & Beverages", "Books", "Office Supplies", "Sports"
        );
        categoryComboBox.setItems(categories);
        
        // Set up supplier dropdown
        ObservableList<String> suppliers = FXCollections.observableArrayList(
            "Tech Suppliers Inc.",
            "Fashion World Ltd.", 
            "Food Distributors Co.",
            "Book Publishers Unlimited",
            "Office Supply Depot",
            "Global Sports Equipment"
        );
        supplierComboBox.setItems(suppliers);
    }

    @FXML
    private void handleAddProduct() {
        if (!validateInput()) {
            return;
        }

        try {
            // Create new product
            Product product = new Product();
            product.setName(nameField.getText().trim());
            product.setSKU(skuField.getText().trim().toUpperCase());
            product.setPrice(Double.parseDouble(priceField.getText()));
            product.setQuantity(Integer.parseInt(quantityField.getText()));
            product.setCategory(categoryComboBox.getValue());
            product.setSupplier(supplierComboBox.getValue());
            product.setDescription(descriptionField.getText().trim());

            // Add to data storage
            productData.addProduct(product);
            
            showMessage("Product '" + product.getName() + "' added successfully!", "success");
            
            // Clear form for next entry
            clearForm();
            
        } catch (Exception e) {
            showMessage("Error adding product: " + e.getMessage(), "error");
        }
    }

    @FXML
    private void handleCancel() {
        // Close the window
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private boolean validateInput() {
        // Validate Product Name
        if (nameField.getText().trim().isEmpty()) {
            showMessage("Product name is required", "error");
            nameField.requestFocus();
            return false;
        }

        // Validate SKU
        if (skuField.getText().trim().isEmpty()) {
            showMessage("SKU is required", "error");
            skuField.requestFocus();
            return false;
        }

        // Validate Price
        try {
            double price = Double.parseDouble(priceField.getText());
            if (price <= 0) {
                showMessage("Price must be greater than 0", "error");
                priceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid price", "error");
            priceField.requestFocus();
            return false;
        }

        // Validate Quantity
        try {
            int quantity = Integer.parseInt(quantityField.getText());
            if (quantity < 0) {
                showMessage("Quantity cannot be negative", "error");
                quantityField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid quantity", "error");
            quantityField.requestFocus();
            return false;
        }

        // Validate Category
        if (categoryComboBox.getValue() == null) {
            showMessage("Please select a category", "error");
            categoryComboBox.requestFocus();
            return false;
        }

        // Validate Supplier
        if (supplierComboBox.getValue() == null) {
            showMessage("Please select a supplier", "error");
            supplierComboBox.requestFocus();
            return false;
        }

        return true;
    }

    private void clearForm() {
        nameField.clear();
        skuField.clear();
        priceField.clear();
        quantityField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        supplierComboBox.getSelectionModel().clearSelection();
        descriptionField.clear();
        nameField.requestFocus();
    }

    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        switch (type) {
            case "error":
                messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                break;
            case "success":
                messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                break;
        }
    }
}