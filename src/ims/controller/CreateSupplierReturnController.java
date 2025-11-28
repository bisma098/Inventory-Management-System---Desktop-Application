package ims.controller;

import ims.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CreateSupplierReturnController {

    @FXML private ComboBox<Supplier> supplierComboBox;
    @FXML private ComboBox<Product> productComboBox;
    @FXML private ComboBox<BatchLot> batchComboBox;
    @FXML private TextField returnQtyField;
    @FXML private TextField reasonField;
    @FXML private TableView<SupplierReturnLine> returnLinesTable;
    @FXML private Label totalAmountLabel;
    @FXML private Label statusLabel;
    @FXML private Label availableQtyLabel;

    private DataController dataController;
    private ObservableList<SupplierReturnLine> returnLinesList;

@FXML
public void initialize() {
    this.dataController = DataController.getInstance();
    this.returnLinesList = FXCollections.observableArrayList();
        
    setupForm();
    setupReturnLinesTable();
}

private void setupForm() {
        // Populate combo boxes
        supplierComboBox.setItems(FXCollections.observableArrayList(dataController.getSuppliers()));
        productComboBox.setItems(FXCollections.observableArrayList(dataController.getProducts()));

        // Set up cell value factories for combo boxes
        setupComboBoxes();
        
        // When supplier changes, filter products from that supplier
        supplierComboBox.setOnAction(event -> {
            productComboBox.setValue(null);
            batchComboBox.setValue(null);
            availableQtyLabel.setText("0");
        });

        // When product changes, load available batches for that supplier-product combination
        productComboBox.setOnAction(event -> {
            loadAvailableBatches();
        });

        // When batch changes, show available quantity
        batchComboBox.setOnAction(event -> {
            updateAvailableQuantity();
        });
    }

private void setupComboBoxes() {
        supplierComboBox.setCellFactory(param -> new ListCell<Supplier>() {
            @Override
            protected void updateItem(Supplier supplier, boolean empty) {
                super.updateItem(supplier, empty);
                if (empty || supplier == null) {
                    setText(null);
                } else {
                    setText(supplier.getName()+ " - " + supplier.getContactInfo());
                }
            }
        });

        supplierComboBox.setButtonCell(new ListCell<Supplier>() {
            @Override
            protected void updateItem(Supplier supplier, boolean empty) {
                super.updateItem(supplier, empty);
                if (empty || supplier == null) {
                    setText("Select Supplier");
                } else {
                    setText(supplier.getName());
                }
            }
        });

        productComboBox.setCellFactory(param -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setText(null);
                } else {
                    setText(product.getSKU() + " - " + product.getName());
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
                    setText(product.getName());
                }
            }
        });


        batchComboBox.setCellFactory(param -> new ListCell<BatchLot>() {
            @Override
            protected void updateItem(BatchLot batch, boolean empty) {
                super.updateItem(batch, empty);
                if (empty || batch == null) {
                    setText(null);
                } else {
                    setText("Batch #" + batch.getBatchId() + " (Qty: " + batch.getAvailableQuantity() + ")");
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

    }

private void loadAvailableBatches() {
    Supplier selectedSupplier = supplierComboBox.getValue();
    Product selectedProduct = productComboBox.getValue();
    
    if (selectedSupplier == null || selectedProduct == null) {
        batchComboBox.setItems(FXCollections.observableArrayList());
        availableQtyLabel.setText("0");
        return;
    }

    // Get batches for this supplier-product combination
    List<BatchLot> availableBatches = dataController.getBatchLots().stream()
        .filter(batch -> {
            // Check if batch has the selected product
            if (batch.getProduct() == null) return false;
            return batch.getProduct().getProductId() == selectedProduct.getProductId();
        })
        .filter(batch -> {
            // Check if this batch came from the selected supplier
            // We need to trace back through purchase order line to purchase order to supplier
            if (batch.getPurchaseOrderLine() == null) return false;
            
            PurchaseOrderLine poLine = batch.getPurchaseOrderLine();
            if (poLine.getProduct() == null) return false;
            
            // Get the purchase order for this line
            PurchaseOrder purchaseOrder = findPurchaseOrderByLineId(poLine.getLineId());
            if (purchaseOrder == null || purchaseOrder.getSupplier() == null) return false;
            
            // Check if the purchase order's supplier matches the selected supplier
            return purchaseOrder.getSupplier().getSupplierId() == selectedSupplier.getSupplierId();
        })
        .filter(batch -> batch.getAvailableQuantity() > 0) // Only batches with available quantity
        .collect(Collectors.toList());

    batchComboBox.setItems(FXCollections.observableArrayList(availableBatches));
    
    if (availableBatches.isEmpty()) {
        showStatus("No available batches found for " + selectedProduct.getName() + " from " + selectedSupplier.getName(), "info");
    } else {
        showStatus("Found " + availableBatches.size() + " batches", "success");
    }
}

// Add this helper method to find purchase order by line ID
private PurchaseOrder findPurchaseOrderByLineId(int lineId) {
    for (PurchaseOrder po : dataController.getPurchaseOrders()) {
        for (PurchaseOrderLine line : po.getOrderLines()) {
            if (line.getLineId() == lineId) {
                return po;
            }
        }
    }
    return null;
}
private void updateAvailableQuantity() {
        BatchLot selectedBatch = batchComboBox.getValue();
        if (selectedBatch != null) {
            availableQtyLabel.setText(String.valueOf(selectedBatch.getAvailableQuantity()));
        } else {
            availableQtyLabel.setText("0");
        }
    }

private void setupReturnLinesTable() {
        // Get columns
        TableColumn<SupplierReturnLine, String> productCol = (TableColumn<SupplierReturnLine, String>) returnLinesTable.getColumns().get(0);
        TableColumn<SupplierReturnLine, String> batchCol = (TableColumn<SupplierReturnLine, String>) returnLinesTable.getColumns().get(1);
        TableColumn<SupplierReturnLine, Integer> quantityCol = (TableColumn<SupplierReturnLine, Integer>) returnLinesTable.getColumns().get(2);
        TableColumn<SupplierReturnLine, Double> unitPriceCol = (TableColumn<SupplierReturnLine, Double>) returnLinesTable.getColumns().get(3);
        TableColumn<SupplierReturnLine, Double> lineTotalCol = (TableColumn<SupplierReturnLine, Double>) returnLinesTable.getColumns().get(4);
        TableColumn<SupplierReturnLine, String> actionCol = (TableColumn<SupplierReturnLine, String>) returnLinesTable.getColumns().get(5);

        // Set up cell value factories
        productCol.setCellValueFactory(cellData -> {
            Product product = cellData.getValue().getProduct();
            return new SimpleStringProperty(product != null ? product.getName() : "");
        });

        batchCol.setCellValueFactory(cellData -> {
            BatchLot batch = cellData.getValue().getBatch();
            return new SimpleStringProperty(batch != null ? "Batch #" + batch.getBatchId() : "");
        });

        quantityCol.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        unitPriceCol.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getUnitPrice()).asObject());

        lineTotalCol.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getTotalPrice()).asObject());

        // Action column with remove button
        actionCol.setCellValueFactory(cellData -> new SimpleStringProperty("Remove"));
        actionCol.setCellFactory(column -> new TableCell<SupplierReturnLine, String>() {
            private final Button removeButton = new Button("Remove");
            {
                removeButton.setStyle("-fx-background-color: #224a47ff; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 4 8;");
                removeButton.setOnAction(event -> {
                    SupplierReturnLine line = getTableView().getItems().get(getIndex());
                    removeReturnLine(line);
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                }
            }
        });

        // Format currency columns
        unitPriceCol.setCellFactory(tc -> new TableCell<SupplierReturnLine, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                }
            }
        });

        lineTotalCol.setCellFactory(tc -> new TableCell<SupplierReturnLine, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", total));
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });

        returnLinesTable.setItems(returnLinesList);
        returnLinesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

@FXML
private void addReturnLine() {
        try {
            if (supplierComboBox.getValue() == null) {
                showStatus("Please select supplier first", "error");
                return;
            }

            Product product = productComboBox.getValue();
            BatchLot batch = batchComboBox.getValue();
            String returnQtyText = returnQtyField.getText();

            if (product == null || batch == null || returnQtyText.isEmpty()) {
                showStatus("Please select product, batch and enter return quantity", "error");
                return;
            }

            int returnQty = Integer.parseInt(returnQtyText);

            // Validate quantity
            if (returnQty <= 0) {
                showStatus("Return quantity must be greater than 0", "error");
                return;
            }

            if (returnQty > batch.getAvailableQuantity()) {
                showStatus("Not enough quantity in this batch. Available: " + batch.getAvailableQuantity(), "error");
                return;
            }

            // Check if this batch is already in return lines
            for (SupplierReturnLine existingLine : returnLinesList) {
                if (existingLine.getBatch() != null && existingLine.getBatch().getBatchId() == batch.getBatchId()) {
                    showStatus("This batch is already in the return list", "error");
                    return;
                }
            }

            // Create new return line
            SupplierReturnLine newLine = new SupplierReturnLine(product, returnQty, batch);
            newLine.setUnitPrice(product.getPrice());
            newLine.setTotalPrice(returnQty * product.getPrice());

            // Add to the list
            returnLinesList.add(newLine);
            supplierComboBox.setDisable(true);

            // Update total
            updateTotalAmount();

            // Clear the form fields for next entry
            productComboBox.setValue(null);
            batchComboBox.setValue(null);
            returnQtyField.clear();
            availableQtyLabel.setText("0");

            showStatus("Product added to return", "success");

        } catch (NumberFormatException e) {
            showStatus("Please enter valid number for return quantity", "error");
        }
    }

private void removeReturnLine(SupplierReturnLine line) {
        returnLinesList.remove(line);
        updateTotalAmount();
        showStatus("Product removed from return", "info");
        
        if (returnLinesList.isEmpty()) {
            supplierComboBox.setDisable(false);
        }
}

private void updateTotalAmount() {
        double total = returnLinesList.stream()
                .mapToDouble(SupplierReturnLine::getTotalPrice)
                .sum();
        totalAmountLabel.setText(String.format("Total: $%.2f", total));
}

@FXML
private void createSupplierReturn() {
        try {
            // Validate
            if (supplierComboBox.getValue() == null) {
                showStatus("Please select a supplier", "error");
                return;
            }

            if (reasonField.getText().isEmpty()) {
                showStatus("Please enter return reason", "error");
                return;
            }

            if (returnLinesList.isEmpty()) {
                showStatus("Please add at least one product to the return", "error");
                return;
            }

            // Create supplier return
            SupplierReturn newReturn = new SupplierReturn();
            newReturn.setSupplier(supplierComboBox.getValue());
            newReturn.setReturnDate(LocalDate.now());
            newReturn.setReason(reasonField.getText());
            newReturn.setTotalAmount(returnLinesList.stream().mapToDouble(SupplierReturnLine::getTotalPrice).sum());

            // Add all return lines
            for (SupplierReturnLine line : returnLinesList) {
                newReturn.addReturnLine(line);
            }

            // Save to database
            dataController.addSupplierReturn(newReturn);
            boolean success = dataController.saveSupplierReturn(newReturn);
            
            if (!success) {
                showStatus("Failed to create supplier return. Please try again.", "error");
                return;
            }

            User currentUser = dataController.getCurrentUser();
            dataController.logUserActivity(
            currentUser.getUserId(),
            "Created Supplier Return #" + newReturn.getId() +" with Supplier "+ newReturn.getSupplier().getName()
            );

            // Update batch quantities and product quantities
            for (SupplierReturnLine line : newReturn.getReturnLines()) {
                BatchLot batch = line.getBatch();
                if (batch != null) {
                    // Decrease batch quantity
                    batch.setAvailableQuantity(batch.getAvailableQuantity() - line.getQuantity());
                    dataController.updateBatchQuantity(batch, -line.getQuantity());
                    
                    // Decrease product quantity
                    line.getProduct().removeQuantity(line.getQuantity());
                    dataController.updateProductQuantity(line.getProduct(), -line.getQuantity());

                    // Log inventory change for each product in the order
                        dataController.logInventoryChange(
                        currentUser.getUserId(),
                        line.getProduct().getProductId(),
                        "Stock decreased by " + line.getQuantity() +
                        " for Product '" + line.getProduct().getName() +
                        "' via Supplier Return #" + newReturn.getId()
                    );

                    dataController.evaluateStockNotification(line.getProduct());
                }
            }

            showStatus("Supplier return created successfully! Return ID: " + newReturn.getId(), "success");
            
            // Clear form and go back after success
            clearForm();
            
            // Optionally auto-navigate back after delay
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(this::goBack);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

        } catch (Exception e) {
            showStatus("Error creating supplier return: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    @FXML
private void goBack() {
        try {
            BorderPane root = (BorderPane) supplierComboBox.getScene().getRoot();
            root.setCenter(FXMLLoader.load(getClass().getResource("/ims/view/SupplierReturn.fxml")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

private void clearForm() {
        supplierComboBox.setValue(null);
        productComboBox.setValue(null);
        batchComboBox.setValue(null);
        returnQtyField.clear();
        reasonField.clear();
        returnLinesList.clear();
        totalAmountLabel.setText("Total: $0.00");
        statusLabel.setVisible(false);
        supplierComboBox.setDisable(false);
        availableQtyLabel.setText("0");
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
            case "info":
                statusLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;");
                break;
        }
    }
}