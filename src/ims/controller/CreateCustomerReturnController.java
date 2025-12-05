package ims.controller;

import ims.model.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import java.time.LocalDate;
import javafx.scene.layout.VBox;

public class CreateCustomerReturnController {

    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private ComboBox<SalesOrder> salesOrderComboBox;
    @FXML private ComboBox<SalesOrderLine> orderLineComboBox;

    @FXML private TextField reasonField;

    @FXML private Label batchLabel;
    @FXML private Label soldQtyLabel;
    @FXML private TextField returnQtyField;

    @FXML private TableView<CustomerReturnLine> returnLinesTable;

    @FXML private Label totalAmountLabel;
    @FXML private Label statusLabel;

    private DataController dataController;
    private ObservableList<CustomerReturnLine> returnLinesList;

    @FXML
    public void initialize() {
        dataController = DataController.getInstance();
        returnLinesList = FXCollections.observableArrayList();

        setupForm();
        setupReturnLinesTable();
    }

    private void setupForm() {
        customerComboBox.setItems(FXCollections.observableArrayList(dataController.getCustomers()));

        customerComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Customer c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getName() + " (" + c.getContactInfo() + ")");
            }
        });

        customerComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Customer c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? "Select Customer" : c.getName());
            }
        });

        customerComboBox.setOnAction(e -> loadSalesOrders());
        salesOrderComboBox.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(SalesOrder so, boolean empty) {
                super.updateItem(so, empty);
                if (empty || so == null) setText(null);
                else setText("Order #" + so.getOrderId() + " — " + so.getOrderDate());
            }
        });

        salesOrderComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(SalesOrder so, boolean empty) {
                super.updateItem(so, empty);
                setText(empty || so == null ? null : "Order #" + so.getOrderId());
            }
        });

        salesOrderComboBox.setOnAction(e -> loadOrderLines());
        
        orderLineComboBox.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(SalesOrderLine line, boolean empty) {
                super.updateItem(line, empty);
                if (empty || line == null) {
                    setText(null);
                } else {
                    Product p = line.getProduct();
                    setText(p.getName() + " — Batch " + line.getBatch().getBatchId()
                            + " — Qty: " + line.getQuantity());
                }
            }
        });

        orderLineComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(SalesOrderLine line, boolean empty) {
                super.updateItem(line, empty);
                if (empty || line == null) setText(null );
                else setText(line.getProduct().getName());
            }
        });

}

        
@FXML
private void loadSalesOrders() {
        Customer c = customerComboBox.getValue();
        if (c == null) return;

        salesOrderComboBox.setItems(
                FXCollections.observableArrayList(dataController.getSalesOrdersByCustomer(c.getId()))
        );

        salesOrderComboBox.getSelectionModel().clearSelection();
        orderLineComboBox.getItems().clear();
        clearLineForm();

        salesOrderComboBox.setOnAction(e -> loadOrderLines());
    }


@FXML
private void loadOrderLines() {
        SalesOrder so = salesOrderComboBox.getValue();
        if (so == null) return;

        orderLineComboBox.setItems(FXCollections.observableArrayList(so.getOrderLines()));
        orderLineComboBox.getSelectionModel().clearSelection();

        clearLineForm();

        orderLineComboBox.setOnAction(e -> showSelectedItemInfo());
}


private void showSelectedItemInfo() {
SalesOrderLine line = orderLineComboBox.getValue();
        if (line == null) return;

        batchLabel.setText("Batch # " + line.getBatch().getBatchId());
        soldQtyLabel.setText(String.valueOf(line.getQuantity())); // sold quantity
}


private void clearLineForm() {
        batchLabel.setText("-");
        soldQtyLabel.setText("0");
        returnQtyField.clear();
}


private void setupReturnLinesTable() {

        TableColumn<CustomerReturnLine, String> productCol =
                (TableColumn<CustomerReturnLine, String>) returnLinesTable.getColumns().get(0);

        TableColumn<CustomerReturnLine, String> batchCol =
                (TableColumn<CustomerReturnLine, String>) returnLinesTable.getColumns().get(1);

        TableColumn<CustomerReturnLine, Integer> qtyCol =
                (TableColumn<CustomerReturnLine, Integer>) returnLinesTable.getColumns().get(2);

        TableColumn<CustomerReturnLine, Double> unitPriceCol =
                (TableColumn<CustomerReturnLine, Double>) returnLinesTable.getColumns().get(3);

        TableColumn<CustomerReturnLine, Double> lineTotalCol =
                (TableColumn<CustomerReturnLine, Double>) returnLinesTable.getColumns().get(4);

        TableColumn<CustomerReturnLine, String> actionCol =
                (TableColumn<CustomerReturnLine, String>) returnLinesTable.getColumns().get(5);

        productCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getBatch().getProduct().getName()));

        batchCol.setCellValueFactory(c ->
                new SimpleStringProperty("Batch # " + c.getValue().getBatch().getBatchId()));

        qtyCol.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getQuantity()).asObject());

        unitPriceCol.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getUnitPrice()).asObject());

        lineTotalCol.setCellValueFactory(c ->
                new SimpleDoubleProperty(
                        c.getValue().getQuantity() * c.getValue().getUnitPrice()
                ).asObject()
        );

        actionCol.setCellValueFactory(c -> new SimpleStringProperty("Remove"));
        actionCol.setCellFactory(col -> new TableCell<>() {
            final Button btn = new Button("Remove");

            {
                btn.setStyle("-fx-background-color:#f44336; -fx-text-fill:white;");
                btn.setOnAction(e -> removeReturnLine(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        returnLinesTable.setItems(returnLinesList);
        returnLinesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }


@FXML
private void addReturnLine() {
    try {
        Customer c = customerComboBox.getValue();
        SalesOrder so = salesOrderComboBox.getValue();
        SalesOrderLine line = orderLineComboBox.getValue();

            if (c == null || so == null || line == null) {
                showStatus("Fill all required fields", "error");
                return;
            }

            int qty = Integer.parseInt(returnQtyField.getText());
            if (qty <= 0) {
                showStatus("Quantity must be greater than 0", "error");
                return;
            }

            if (qty > line.getQuantity()) {
                showStatus("Cannot return more than purchased amount", "error");
                return;
            }

        boolean exists = returnLinesList.stream()
                .anyMatch(r -> r.getBatch().getBatchId() == line.getBatch().getBatchId());

        if (exists) {
            showStatus("This item is already added to the return", "error");
            return;
        }

            CustomerReturnLine returnLine = new CustomerReturnLine();
            returnLine.setBatch(line.getBatch());
            returnLine.setUnitPrice(line.getUnitPrice());
            returnLine.setQuantity(qty);
            returnLine.setProduct(line.getProduct());
            returnLine.setSubTotal(qty*line.getUnitPrice());

            returnLinesList.add(returnLine);
            customerComboBox.setDisable(true);
            updateTotal();
            clearLineForm();

            showStatus("Item added", "success");

        } catch (Exception e) {
            showStatus("Invalid quantity", "error");
        }
}


private void removeReturnLine(CustomerReturnLine l) {
        returnLinesList.remove(l);
        updateTotal();
        showStatus("Removed", "info");
        if (returnLinesList.isEmpty()) {
            customerComboBox.setDisable(false);
        }
}


private void updateTotal() {
        double total = returnLinesList.stream()
                .mapToDouble(l -> l.getQuantity() * l.getUnitPrice())
                .sum();

        totalAmountLabel.setText(String.format("Total: $%.2f", total));
}


 @FXML
private void createCustomerReturn() {
        try {
            if (customerComboBox.getValue() == null) {
                showStatus("Select customer", "error");
                return;
            }
            if (reasonField.getText().isEmpty()) {
                showStatus("Enter a return reason", "error");
                return;
            }
            if (returnLinesList.isEmpty()) {
                showStatus("Add at least 1 return item", "error");
                return;
            }

            CustomerReturn cr = new CustomerReturn();
            cr.setCustomer(customerComboBox.getValue());
            cr.setReturnDate(LocalDate.now());
            cr.setReason(reasonField.getText());

            for (CustomerReturnLine line : returnLinesList) {
                cr.addReturnLine(line);
            }

            dataController.addCustomerReturn(cr);
            boolean saved = dataController.saveCustomerReturn(cr);

            if (!saved) {
                showStatus("Failed to save return", "error");
                return;
            }

            User currentUser = dataController.getCurrentUser();
            dataController.logUserActivity(
            currentUser.getUserId(),
            "Created Customer Return #" + cr.getReturnId() +" for Customer "+ cr.getCustomer().getName()
            );

     
            for (CustomerReturnLine line : cr.getReturnLines()) {
                BatchLot batch = line.getBatch();
                batch.addQuantity(line.getQuantity());
                dataController.updateBatchQuantity(batch,line.getQuantity());

                Product p = batch.getProduct();
                p.addQuantity(line.getQuantity());
                dataController.updateProductQuantity(p, line.getQuantity());

                        dataController.logInventoryChange(
                        currentUser.getUserId(),
                        line.getProduct().getProductId(),
                        "Stock increased by " + line.getQuantity() +
                        " for Product '" + line.getProduct().getName() +
                        "' via Customer Return #" + cr.getReturnId()
                    );
            }

            showStatus("Return Created! ID: " + cr.getReturnId(), "success");

            clearAll();
            goBack();

        } catch (Exception e) {
            e.printStackTrace();
            showStatus("Error: " + e.getMessage(), "error");
        }
    }


    private void clearAll() {
        customerComboBox.setValue(null);
        salesOrderComboBox.getItems().clear();
        orderLineComboBox.getItems().clear();

        reasonField.clear();

        clearLineForm();
        returnLinesList.clear();
        updateTotal();
    }


    @FXML
    private void goBack() {
        try {
            BorderPane root = (BorderPane) customerComboBox.getScene().getRoot();
            root.setCenter(FXMLLoader.load(getClass().getResource("/ims/view/CustomerReturn.fxml")));
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
