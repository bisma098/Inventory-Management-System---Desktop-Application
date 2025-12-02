package ims.controller;

import ims.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ManageStockController {
    
    @FXML private ComboBox<Product> productComboBox;
    @FXML private ComboBox<BatchLot> batchComboBox;
    @FXML private TextField quantityField;
    @FXML private ComboBox<String> actionComboBox;
    @FXML private TextArea reasonField;
    @FXML private Label currentStockLabel;
    @FXML private Label batchStockLabel;
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

        // Setup batch combo box
        batchComboBox.setCellFactory(param -> new ListCell<BatchLot>() {
            @Override
            protected void updateItem(BatchLot batch, boolean empty) {
                super.updateItem(batch, empty);
                if (empty || batch == null) {
                    setText(null);
                } else {
                    setText("Batch #" + batch.getBatchId() + " - Qty: " + batch.getAvailableQuantity());
                }
            }
        });

        batchComboBox.setButtonCell(new ListCell<BatchLot>() {
            @Override
            protected void updateItem(BatchLot batch, boolean empty) {
                super.updateItem(batch, empty);
                if (empty || batch == null) {
                    setText("Select Batch");
                } else {
                    setText("Batch #" + batch.getBatchId());
                }
            }
        });

        // Event handler for product selection
        productComboBox.setOnAction(event -> {
            updateSelectedProductInfo();
            loadBatchesForProduct();
        });

        // Update batch info when batch is selected
        batchComboBox.setOnAction(event -> updateBatchInfo());
    }

    private void loadBatchesForProduct() {
        Product product = productComboBox.getValue();
        if (product == null) {
            batchComboBox.setItems(FXCollections.observableArrayList());
            batchStockLabel.setText("Batch Stock: 0");
            return;
        }

        ObservableList<BatchLot> batches = FXCollections.observableArrayList(
            dataController.getBatchesByProductId(product.getProductId())
        );

        batchComboBox.setItems(batches);
        batchComboBox.getSelectionModel().clearSelection();
        batchStockLabel.setText("Batch Stock: 0");
    }

    private void updateBatchInfo() {
        BatchLot selectedBatch = batchComboBox.getValue();
        if (selectedBatch != null) {
            batchStockLabel.setText("Batch Stock: " + selectedBatch.getAvailableQuantity());
        } else {
            batchStockLabel.setText("Batch Stock: 0");
        }
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
        
        // Clear batch selection when product changes
        batchComboBox.setValue(null);
        batchStockLabel.setText("Batch Stock: 0");
    }

    @FXML
    private void updateStock() {
        Product product = productComboBox.getValue();
        BatchLot batch = batchComboBox.getValue();
        String action = actionComboBox.getValue();
        String quantityText = quantityField.getText();
        String reason = reasonField.getText();

        // 1. Validate
        if (product == null || batch == null || action == null || quantityText.isEmpty() || reason.isEmpty()) {
            showStatus("Please fill all required fields including batch", "error");
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

        // Validate batch quantity for removal actions
        if (action.equals("Remove Stock")) {
            if (qty > batch.getAvailableQuantity()) {
                showStatus("Cannot remove more than available batch stock: " + batch.getAvailableQuantity(), "error");
                return;
            }
        }

        try {
            // 2. Update DB
            int changeQty = action.equals("Add Stock") ? qty : -qty;

            // Update batch
            if (changeQty > 0) {
                batch.setAvailableQuantity(batch.getAvailableQuantity() + qty);
                batch.setTotalQuantity(batch.getTotalQuantity() + qty);
            } else {
                batch.setAvailableQuantity(batch.getAvailableQuantity() - qty);
            }
            dataController.updateBatchQuantity(batch, changeQty);

            // 4. Update Java object & refresh display
            if (changeQty > 0) {
                product.addQuantity(qty);
            } else {
                product.removeQuantity(qty);
            }
            
            // Update product total quantity
            dataController.updateProductQuantity(product, changeQty);

            User currentUser = dataController.getCurrentUser();
            dataController.logUserActivity(
            currentUser.getUserId(),
            "Updated Stock Level for " + product.getName() +" by Quantity "+ changeQty
            );

            if (changeQty<0){
            dataController.logInventoryChange(
                        currentUser.getUserId(),
                        product.getProductId(),
                        "Stock decreased by " + qty +
                        " for Product '" + product.getName() +
                        "'via Adjust Stock'"
                    );
                }
            else{

                dataController.logInventoryChange(
                        currentUser.getUserId(),
                        product.getProductId(),
                        "Stock increased by " + qty +
                        " for Product '" + product.getName() +
                        "'via Adjust Stock'");
            }

            // Refresh the displayed information
            updateSelectedProductInfo();
            updateBatchInfo();
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
        batchComboBox.setValue(null);
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