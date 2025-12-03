package ims.controller;

import ims.model.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class CreateSalesOrderController {

    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private ComboBox<Product> productComboBox;
    @FXML private ComboBox<BatchLot> batchComboBox;

    @FXML private Label availableQtyLabel;

    @FXML private TextField saleQtyField;
    @FXML private TextField unitPriceField;

    @FXML private TableView<SalesOrderLine> orderLinesTable;
    @FXML private Label totalAmountLabel;
    @FXML private Label statusLabel;
    @FXML private VBox newCustomerBox;
    @FXML private TextField customerNameField;
    @FXML private TextField customerContactField;


    private DataController dataController;
    private ObservableList<SalesOrderLine> orderLinesList;

    @FXML
    public void initialize() {
        dataController = DataController.getInstance();
        orderLinesList = FXCollections.observableArrayList();

        setupForm();
        setupOrderLinesTable();
    }

    // ------------------------------------------------------------
    // FORM SETUP
    // ------------------------------------------------------------
    
    private void setupForm() {

        // Load customers
        customerComboBox.setItems(FXCollections.observableArrayList(dataController.getCustomers()));
        productComboBox.setItems(FXCollections.observableArrayList(dataController.getProducts()));

        // Customer display format
        customerComboBox.setCellFactory(param -> new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) setText(null);
                else setText(c.getName() + " (" + c.getContactInfo() + ")");
            }
        });

        customerComboBox.setButtonCell(new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) setText("Select Customer");
                else setText(c.getName() + " (" + c.getContactInfo() + ")");
            }
        });

        // Product combo display
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
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) setText("Select Product");
                else setText(p.getName());
            }
        });

        batchComboBox.setCellFactory(param -> new ListCell<BatchLot>() {
            @Override
            protected void updateItem(BatchLot batch, boolean empty) {
                super.updateItem(batch, empty);
                if (empty || batch == null) {
                    setText(null);
                } else {
                    setText("Batch # " + batch.getBatchId());
                }
            }
        });

        batchComboBox.setButtonCell(new ListCell<BatchLot>() {
            @Override
            protected void updateItem(BatchLot p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) setText("Select Batch");
                else setText("Batch # " + p.getBatchId());
            }
        });

        // When product is selected → load available batches
        productComboBox.setOnAction(e -> loadBatchesForProduct());
    }

// LOAD BATCHES FOR SELECTED PRODUCT
@FXML
private void loadBatchesForProduct() {
        Product p = productComboBox.getValue();
        if (p == null) return;

        ObservableList<BatchLot> batches = FXCollections.observableArrayList(
            dataController.getBatchesByProductId(p.getProductId())
        );

        batchComboBox.setItems(batches);
        batchComboBox.getSelectionModel().clearSelection();
        availableQtyLabel.setText("0");
        unitPriceField.clear();

        // Load batch info on select
        batchComboBox.setOnAction(e -> showBatchInfo());
    }

    @FXML
    private void showBatchInfo() {
        BatchLot batch = batchComboBox.getValue();
        if (batch == null) return;

        availableQtyLabel.setText(String.valueOf(batch.getAvailableQuantity()));
        unitPriceField.setText(String.format("%.2f", batch.getProduct().getPrice()));
    }

// ORDER LINE TABLE SETUP
private void setupOrderLinesTable() {
        TableColumn<SalesOrderLine, String> productCol =
                (TableColumn<SalesOrderLine, String>) orderLinesTable.getColumns().get(0);

        TableColumn<SalesOrderLine, String> batchCol =
                (TableColumn<SalesOrderLine, String>) orderLinesTable.getColumns().get(1);

        TableColumn<SalesOrderLine, Integer> qtyCol =
                (TableColumn<SalesOrderLine, Integer>) orderLinesTable.getColumns().get(2);

        TableColumn<SalesOrderLine, Double> unitPriceCol =
                (TableColumn<SalesOrderLine, Double>) orderLinesTable.getColumns().get(3);

        TableColumn<SalesOrderLine, Double> subtotalCol =
                (TableColumn<SalesOrderLine, Double>) orderLinesTable.getColumns().get(4);

        TableColumn<SalesOrderLine, String> actionCol =
                (TableColumn<SalesOrderLine, String>) orderLinesTable.getColumns().get(5);

        productCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getBatch().getProduct().getName()));

        batchCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getBatch().getBatchId() + ""));

        qtyCol.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getQuantity()).asObject());

        unitPriceCol.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getUnitPrice()).asObject());

        subtotalCol.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getQuantity()*c.getValue().getUnitPrice()).asObject());

        // Remove button
        actionCol.setCellValueFactory(c -> new SimpleStringProperty("Remove"));
        actionCol.setCellFactory(col -> new TableCell<SalesOrderLine, String>() {
            final Button btn = new Button("Remove");

            {
                btn.setStyle("-fx-background-color:#f44336; -fx-text-fill:white;");
                btn.setOnAction(e -> {
                    SalesOrderLine line = getTableView().getItems().get(getIndex());
                    removeOrderLine(line);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        orderLinesTable.setItems(orderLinesList);
        orderLinesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


    }

// ADD ORDER LINE
@FXML
private void addSalesOrderLine() {
        try {
            Customer c = customerComboBox.getValue();
            Product p = productComboBox.getValue();
            BatchLot batch = batchComboBox.getValue();
            String qtyText = saleQtyField.getText();    

            if (c == null) {
                showStatus("Select customer first", "error");
                return;
            }
            if (p == null || batch == null || qtyText.isEmpty()) {
                showStatus("Fill all fields", "error");
                return;
            }

            int qty = Integer.parseInt(qtyText);
            if (qty <= 0) {
                showStatus("Quantity must be greater than 0", "error");
                return;
            }

            if (qty > batch.getAvailableQuantity()) {
                showStatus("Not enough stock in selected batch!", "error");
                return;
            }

            SalesOrderLine line = new SalesOrderLine();
            line.setBatch(batch);
            line.setQuantity(qty);
            line.setProduct(p);
            line.setUnitPrice(batch.getProduct().getPrice());
            line.setSubTotal(qty * line.getUnitPrice());


            orderLinesList.add(line);
            customerComboBox.setDisable(true);

            updateTotal();
            clearLineForm();

            showStatus("Item added", "success");

        } catch (Exception e) {
            showStatus("Invalid quantity", "error");
        }
    }

    private void clearLineForm() {
        productComboBox.getSelectionModel().clearSelection();
        batchComboBox.getSelectionModel().clearSelection();
        availableQtyLabel.setText("0");
        saleQtyField.clear();
        unitPriceField.clear();
    }

    private void removeOrderLine(SalesOrderLine line) {
        orderLinesList.remove(line);
        updateTotal();

        if (orderLinesList.isEmpty()) {
            customerComboBox.setDisable(false);
        }

        showStatus("Removed", "info");
}

// TOTAL
private void updateTotal() {
    double total = orderLinesList.stream()
            .mapToDouble(SalesOrderLine::getSubTotal)
            .sum();

    totalAmountLabel.setText(String.format("Total: $%.2f", total));
}

// SAVE SALES ORDER

@FXML
private void toggleCustomerForm() {
    if (newCustomerBox.isVisible()) {
        newCustomerBox.setVisible(false);
        newCustomerBox.setManaged(false);
    } else {
        newCustomerBox.setVisible(true);
        newCustomerBox.setManaged(true);
    }
}

@FXML
private void saveNewCustomer() {
    String name = customerNameField.getText();
    String contact = customerContactField.getText();

    if (name.isEmpty() || contact.isEmpty()) {
        showStatus("Please enter customer name and contact", "error");
        return;
    }

    Customer customer = new Customer();
    customer.setName(name);
    customer.setContactInfo(contact);

    boolean saved = dataController.saveCustomer(customer);

    if (!saved) {
        showStatus("Failed to save customer. Try again.", "error");
        return;
    }

    dataController.addCustomer(customer);

    customerComboBox.getItems().add(customer);
    customerComboBox.setValue(customer);

    // Hide form
    newCustomerBox.setVisible(false);
    customerNameField.clear();
    customerContactField.clear();

    showStatus("Customer added successfully!", "success");
}

@FXML
private void createSalesOrder() {
        try {
            if (customerComboBox.getValue() == null) {
                showStatus("Select customer", "error");
                return;
            }
            if (orderLinesList.isEmpty()) {
                showStatus("Add at least 1 item", "error");
                return;
            }

            SalesOrder so = new SalesOrder();
            so.setCustomer(customerComboBox.getValue());
            so.setOrderDate(java.time.LocalDate.now());

            for (SalesOrderLine line : orderLinesList) {
                so.getOrderLines().add(line);
            }

            dataController.addSalesOrder(so);
            boolean saved = dataController.saveSalesOrder(so);

            if (!saved) {
                showStatus("Failed to save sales order", "error");
                return;
            }

            User currentUser = dataController.getCurrentUser();
            dataController.logUserActivity(
            currentUser.getUserId(),
            "Created Sales Order #" + so.getOrderId() +" with Customer "+ so.getCustomer().getName()
            );

            
            for (SalesOrderLine line : so.getOrderLines()) {

                BatchLot batch = line.getBatch();
                batch.updateBatchQuantity(line.getQuantity());
                dataController.updateBatchQuantity(batch, batch.getAvailableQuantity());

                Product p = batch.getProduct();
                p.removeQuantity(line.getQuantity());
                dataController.updateProductQuantity(p, -line.getQuantity());

               
                        dataController.logInventoryChange(
                        currentUser.getUserId(),
                        line.getProduct().getProductId(),
                        "Stock decreased by " + line.getQuantity() +
                        " for Product '" + line.getProduct().getName() +
                        "' via Sales Order #" + so.getOrderId()
                    );

                    ///sdataController.evaluateStockNotification(line.getProduct());
            }

            showStatus("Sales Order Created! ID: " + so.getOrderId(), "success");

            clearAll();
            goBack();

        } catch (Exception e) {
            e.printStackTrace();
            showStatus("Error: " + e.getMessage(), "error");
        }
    }

    private void clearAll() {
        customerComboBox.setValue(null);
        customerComboBox.setDisable(false);
        productComboBox.setValue(null);
        batchComboBox.setValue(null);

        saleQtyField.clear();
        availableQtyLabel.setText("0");
        unitPriceField.clear();

        orderLinesList.clear();
        updateTotal();
    }

@FXML
private void goBack() {
        try {
            BorderPane root = (BorderPane) customerComboBox.getScene().getRoot();
            root.setCenter(FXMLLoader.load(getClass().getResource("/ims/view/SalesOrder.fxml")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 private void showStatus(String msg, String type) {
        statusLabel.setText(msg);
        statusLabel.setVisible(true);

        switch (type) {
            case "success":
                statusLabel.setStyle("-fx-text-fill:#4CAF50; -fx-font-weight:bold;");
                break;
            case "error":
                statusLabel.setStyle("-fx-text-fill:#f44336; -fx-font-weight:bold;");
                break;
            default:
                statusLabel.setStyle("-fx-text-fill:#2196F3; -fx-font-weight:bold;");
        }
    }
}
