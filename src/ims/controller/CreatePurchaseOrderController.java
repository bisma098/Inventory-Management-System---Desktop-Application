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

public class CreatePurchaseOrderController {

    @FXML private ComboBox<Supplier> supplierComboBox;
    @FXML private ComboBox<Product> productComboBox;
    @FXML private ComboBox<Warehouse> warehouseComboBox;
    @FXML private TextField quantityField;
    @FXML private TextField unitPriceField;
    @FXML private TableView<PurchaseOrderLine> orderLinesTable;
    @FXML private Label totalAmountLabel;
    @FXML private Label statusLabel;
    @FXML private DatePicker manufactureDatePicker;
    @FXML private DatePicker expiryDatePicker;


    private DataController dataController;
    private ObservableList<PurchaseOrderLine> orderLinesList;

    @FXML
    public void initialize() {
        this.dataController = DataController.getInstance();
        this.orderLinesList = FXCollections.observableArrayList();
        
        setupForm();
        setupOrderLinesTable();
    }

    private void setupForm() {
        // Populate combo boxes
        supplierComboBox.setItems(FXCollections.observableArrayList(dataController.getSuppliers()));
        productComboBox.setItems(FXCollections.observableArrayList(dataController.getProducts()));
        warehouseComboBox.setItems(FXCollections.observableArrayList(dataController.getWarehouses()));

        // Set up cell value factories for combo boxes
        supplierComboBox.setCellFactory(param -> new ListCell<Supplier>() {
            @Override
            protected void updateItem(Supplier supplier, boolean empty) {
                super.updateItem(supplier, empty);
                if (empty || supplier == null) {
                    setText(null);
                } else {
                    setText(supplier.getName() + " - " + supplier.getContactInfo());
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
                    setText(supplier.getName() + " - " + supplier.getContactInfo());
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
                    setText(product.getName() + " - $" + product.getPrice());
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
                    setText(product.getName() + " - $" + product.getPrice());
                }
            }
        });

        warehouseComboBox.setCellFactory(param -> new ListCell<Warehouse>() {
            @Override
            protected void updateItem(Warehouse warehouse, boolean empty) {
                super.updateItem(warehouse, empty);
                if (empty || warehouse == null) {
                    setText(null);
                } else {
                    setText(warehouse.getWarehouseName() + " - " + warehouse.getAddress());
                }
            }
        });

        warehouseComboBox.setButtonCell(new ListCell<Warehouse>() {
            @Override
            protected void updateItem(Warehouse warehouse, boolean empty) {
                super.updateItem(warehouse, empty);
                if (empty || warehouse == null) {
                    setText("Select Warehouse");
                } else {
                    setText(warehouse.getWarehouseName() + " - " + warehouse.getAddress());
                }
            }
        });

        // Auto-fill unit price when product is selected
        productComboBox.setOnAction(event -> {
            Product selectedProduct = productComboBox.getValue();
            if (selectedProduct != null) {
                unitPriceField.setText(String.format("%.2f", selectedProduct.getPrice()));
            }
        });
    }

    private void setupOrderLinesTable() {
        // Get columns
        TableColumn<PurchaseOrderLine, String> productCol = (TableColumn<PurchaseOrderLine, String>) orderLinesTable.getColumns().get(0);
        TableColumn<PurchaseOrderLine, String> warehouseCol = (TableColumn<PurchaseOrderLine, String>) orderLinesTable.getColumns().get(1);
        TableColumn<PurchaseOrderLine, Integer> quantityCol = (TableColumn<PurchaseOrderLine, Integer>) orderLinesTable.getColumns().get(2);
        TableColumn<PurchaseOrderLine, Double> unitPriceCol = (TableColumn<PurchaseOrderLine, Double>) orderLinesTable.getColumns().get(3);
        TableColumn<PurchaseOrderLine, Double> lineTotalCol = (TableColumn<PurchaseOrderLine, Double>) orderLinesTable.getColumns().get(4);
        TableColumn<PurchaseOrderLine, String> actionCol = (TableColumn<PurchaseOrderLine, String>) orderLinesTable.getColumns().get(5);

        // Set up cell value factories
        productCol.setCellValueFactory(cellData -> {
            Product product = cellData.getValue().getProduct();
            return new SimpleStringProperty(product != null ? product.getName() : "");
        });

        warehouseCol.setCellValueFactory(cellData -> {
            Warehouse warehouse = cellData.getValue().getWarehouse();
            return new SimpleStringProperty(warehouse != null ? warehouse.getWarehouseName() : "");
        });

        quantityCol.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        unitPriceCol.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getUnitPrice()).asObject());

        lineTotalCol.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().calculateLineTotal()).asObject());

        // Action column with remove button
        actionCol.setCellValueFactory(cellData -> new SimpleStringProperty("Remove"));
        actionCol.setCellFactory(column -> new TableCell<PurchaseOrderLine, String>() {
            private final Button removeButton = new Button("Remove");
            
            {
                removeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 4 8;");
                removeButton.setOnAction(event -> {
                    PurchaseOrderLine line = getTableView().getItems().get(getIndex());
                    removeOrderLine(line);
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
        unitPriceCol.setCellFactory(tc -> new TableCell<PurchaseOrderLine, Double>() {
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

        lineTotalCol.setCellFactory(tc -> new TableCell<PurchaseOrderLine, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", total));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #008080;");
                }
            }
        });

        orderLinesTable.setItems(orderLinesList);
        orderLinesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void addOrderLine() {
        try {
            if (supplierComboBox.getValue() == null) {
    showStatus("Please select supplier first", "error");
    return;
}
            Product product = productComboBox.getValue();
            Warehouse warehouse = warehouseComboBox.getValue();
            String quantityText = quantityField.getText();
            String unitPriceText = unitPriceField.getText();

            LocalDate manDate = manufactureDatePicker.getValue();
            LocalDate expDate = expiryDatePicker.getValue();

            if (manDate == null) {
                showStatus("Please select manufacture date", "error");
                return;
            }

            if (product == null || warehouse == null || quantityText.isEmpty() || unitPriceText.isEmpty()) {
                showStatus("Please fill all fields: Product, Warehouse, Quantity, and Unit Price", "error");
                return;
            }

            int quantity = Integer.parseInt(quantityText);
            double unitPrice = Double.parseDouble(unitPriceText);

            if (quantity <= 0 || unitPrice <= 0) {
                showStatus("Quantity and Unit Price must be greater than 0", "error");
                return;
            }

            // Create new order line
            PurchaseOrderLine newLine = new PurchaseOrderLine();
            newLine.setProduct(product);
            newLine.setWarehouse(warehouse);
            newLine.setQuantity(quantity);
            newLine.setUnitPrice(unitPrice);
            newLine.setManufactureDate(manDate);
            newLine.setExpiryDate(expDate);  // can be null


            // Add to the list
            orderLinesList.add(newLine);
            supplierComboBox.setDisable(true);

            // Update total
            updateTotalAmount();

            // Clear the form fields for next entry
            productComboBox.setValue(null);
            warehouseComboBox.setValue(null);
            quantityField.clear();
            unitPriceField.clear();
            manufactureDatePicker.setValue(null);
            expiryDatePicker.setValue(null);


            showStatus("Product added to order", "success");

        } catch (NumberFormatException e) {
            showStatus("Please enter valid numbers for Quantity and Unit Price", "error");
        }
    }

    private void removeOrderLine(PurchaseOrderLine line) {
        orderLinesList.remove(line);
        updateTotalAmount();
        showStatus("Product removed from order", "info");
    }

    private void updateTotalAmount() {
        double total = orderLinesList.stream()
                .mapToDouble(PurchaseOrderLine::calculateLineTotal)
                .sum();
        totalAmountLabel.setText(String.format("Total: $%.2f", total));
    }

@FXML
private void createPurchaseOrder() {
    try {
        // Validate
        if (supplierComboBox.getValue() == null) {
            showStatus("Please select a supplier", "error");
            return;
        }

        if (orderLinesList.isEmpty()) {
            showStatus("Please add at least one product to the order", "error");
            return;
        }

        // Create purchase order
        PurchaseOrder newOrder = new PurchaseOrder();
        newOrder.setSupplier(supplierComboBox.getValue());
        newOrder.setOrderDate(LocalDate.now());

        // Add all order lines
        for (PurchaseOrderLine line : orderLinesList) {
            newOrder.getOrderLines().add(line);
        }

        dataController.addPurchaseOrder(newOrder);
        boolean success = dataController.savePurchaseOrder(newOrder);
        
        if (!success) {
            showStatus("Failed to create purchase order. Please try again.", "error");
            return;
        }

        // Log user activity
        User currentUser = dataController.getCurrentUser();
        dataController.logUserActivity(
            currentUser.getUserId(),
            "Created Purchase Order #" + newOrder.getOrderId() +" with Supplier "+ newOrder.getSupplier().getName()
        );

        for (PurchaseOrderLine line : newOrder.getOrderLines()) {  
            BatchLot batch = new BatchLot();
            batch.setProduct(line.getProduct());
            batch.setWarehouse(line.getWarehouse());
            batch.setTotalQuantity(line.getQuantity());
            batch.setAvailableQuantity(line.getQuantity());
            batch.setManufactureDate(line.getManufactureDate());
            batch.setExpiryDate(line.getExpiryDate());

            batch.setPurchaseOrderLineId(line.getLineId());
            
            boolean batchSaved = dataController.saveBatch(batch);
            dataController.addBatch(batch);
            
            if (batchSaved) {
                line.setBatchLot(batch);
                batch.setPurchaseOrderLine(line);
            } else {
                showStatus("Warning: Failed to save batch for product " + line.getProduct().getName(), "error");
            }

            //update product quantity:
            line.getProduct().addQuantity(line.getQuantity());
            dataController.updateProductQuantity(line.getProduct(),line.getQuantity());
            // Log inventory change for each product in the order
            dataController.logInventoryChange(
                currentUser.getUserId(),
                line.getProduct().getProductId(),
                "Stock increased by " + line.getQuantity() +
                " for Product '" + line.getProduct().getName() +
                "' via Purchase Order #" + newOrder.getOrderId()
            );

            dataController.evaluateStockNotification(line.getProduct());

        }

        showStatus("Purchase order created successfully! Order ID: " + newOrder.getOrderId(), "success");
        
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
        showStatus("Error creating purchase order: " + e.getMessage(), "error");
        e.printStackTrace();
    }
}

@FXML
private void goBack() {
    try {
        BorderPane root = (BorderPane) supplierComboBox.getScene().getRoot();
        root.setCenter(FXMLLoader.load(getClass().getResource("/ims/view/PurchaseOrder.fxml")));
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private void clearForm() {
        supplierComboBox.setValue(null);
        productComboBox.setValue(null);
        warehouseComboBox.setValue(null);
        quantityField.clear();
        unitPriceField.clear();
        orderLinesList.clear();
        totalAmountLabel.setText("Total: $0.00");
        statusLabel.setVisible(false);
        supplierComboBox.setDisable(false);

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