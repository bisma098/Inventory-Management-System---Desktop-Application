package ims.controller;

import ims.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import java.time.format.DateTimeFormatter;

public class SalesOrderController {
    
    @FXML private TableView<SalesOrder> salesOrdersTable;
    @FXML private TableView<SalesOrderLine> orderLinesTable;
    @FXML private VBox orderDetailsSection;
    @FXML private Label customerNameLabel;
    @FXML private Label customerContactLabel;
    @FXML private Label detailsTitle;
    @FXML private VBox customerDetailsBox;

    
    private DataController dataController;
    private ObservableList<SalesOrder> salesOrdersList;
    private ObservableList<SalesOrderLine> currentOrderLinesList;
    private SalesOrder selectedOrder;
    
    @FXML
    public void initialize() {
        this.dataController = DataController.getInstance();
        this.salesOrdersList = FXCollections.observableArrayList();
        this.currentOrderLinesList = FXCollections.observableArrayList();
        
        setupSalesOrdersTable();
        setupOrderLinesTable();
        loadSalesOrders();
        
        orderDetailsSection.setVisible(false);
    }
    
    private void setupSalesOrdersTable() {
        TableColumn<SalesOrder, Integer> orderIdCol = (TableColumn<SalesOrder, Integer>) salesOrdersTable.getColumns().get(0);
        TableColumn<SalesOrder, String> customerCol = (TableColumn<SalesOrder, String>) salesOrdersTable.getColumns().get(1);
        TableColumn<SalesOrder, String> dateCol = (TableColumn<SalesOrder, String>) salesOrdersTable.getColumns().get(2);
        TableColumn<SalesOrder, Double> totalCol = (TableColumn<SalesOrder, Double>) salesOrdersTable.getColumns().get(3);
        TableColumn<SalesOrder, Integer> itemsCol = (TableColumn<SalesOrder, Integer>) salesOrdersTable.getColumns().get(4);
        TableColumn<SalesOrder, String> actionCol = (TableColumn<SalesOrder, String>) salesOrdersTable.getColumns().get(5);
        
         salesOrdersTable.setItems(salesOrdersList);
        orderIdCol.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getOrderId()).asObject());
        
        customerCol.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue().getCustomer();
            return new SimpleStringProperty(customer != null ? customer.getName() : "Unknown");
        });
        
        dateCol.setCellValueFactory(cellData -> {
            String formattedDate = cellData.getValue().getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return new SimpleStringProperty(formattedDate);
        });
        
        totalCol.setCellValueFactory(cellData -> {
            double total = cellData.getValue().getOrderLines().stream()
                    .mapToDouble(line -> line.getQuantity() * line.getProduct().getPrice())
                    .sum();
            return new SimpleDoubleProperty(total).asObject();
        });
        
        itemsCol.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getOrderLines().size()).asObject());
        
        actionCol.setCellValueFactory(cellData -> new SimpleStringProperty("View Details"));
        actionCol.setCellFactory(column -> new TableCell<SalesOrder, String>() {
            private final Button viewDetailsButton = new Button("View Details");
            {
                viewDetailsButton.setStyle("-fx-background-color: linear-gradient(to bottom, #008080, #006666); -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 4 8;");
                viewDetailsButton.setOnAction(event -> {
                    SalesOrder order = getTableView().getItems().get(getIndex());
                    showOrderDetails(order);
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewDetailsButton);
                }
            }
        });
        
        totalCol.setCellFactory(tc -> new TableCell<SalesOrder, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("$%.2f", total));
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });
        
        salesOrdersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private void setupOrderLinesTable() {
        TableColumn<SalesOrderLine, String> productCol = (TableColumn<SalesOrderLine, String>) orderLinesTable.getColumns().get(0);
        TableColumn<SalesOrderLine, String> warehouseCol = (TableColumn<SalesOrderLine, String>) orderLinesTable.getColumns().get(1);
        TableColumn<SalesOrderLine, String> batchCol = (TableColumn<SalesOrderLine, String>) orderLinesTable.getColumns().get(2); 
        TableColumn<SalesOrderLine, Integer> quantityCol = (TableColumn<SalesOrderLine, Integer>) orderLinesTable.getColumns().get(3);
        TableColumn<SalesOrderLine, Double> unitPriceCol = (TableColumn<SalesOrderLine, Double>) orderLinesTable.getColumns().get(4);
        TableColumn<SalesOrderLine, Double> lineTotalCol = (TableColumn<SalesOrderLine, Double>) orderLinesTable.getColumns().get(5);

        
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
            new SimpleDoubleProperty(cellData.getValue().getQuantity() * cellData.getValue().getProduct().getPrice()).asObject());
        
        batchCol.setCellValueFactory(cellData -> {
            BatchLot batchLot = cellData.getValue().getBatch();
            if (batchLot != null) {
                return new SimpleStringProperty("Batch #" + batchLot.getBatchId());
            } else {
                return new SimpleStringProperty("No Batch");
            }
        });
        
        unitPriceCol.setCellFactory(tc -> new TableCell<SalesOrderLine, Double>() {
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
        
        lineTotalCol.setCellFactory(tc -> new TableCell<SalesOrderLine, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", total));
                    setStyle("-fx-font-weight: bold");
                }
            }

        });
        
        
        orderLinesTable.setItems(currentOrderLinesList);
        orderLinesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private void loadSalesOrders() {
        salesOrdersList.clear();
        salesOrdersList.addAll(dataController.getSalesOrders());
      
    }
    
    private void showOrderDetails(SalesOrder order) {
        selectedOrder = order;
        
        currentOrderLinesList.clear();
        currentOrderLinesList.addAll(order.getOrderLines());
        
        Label titleLabel = (Label) orderDetailsSection.getChildren().get(0);
        titleLabel.setText("Order Details for Sales Order ID- " + order.getOrderId());
        
        Customer c = order.getCustomer();
        customerNameLabel.setText("Name: " + c.getName());
        customerContactLabel.setText("Contact: " + c.getContactInfo());
        
        orderDetailsSection.setVisible(true);
        
    }
    
    @FXML
    private void hideOrderDetails() {
        orderDetailsSection.setVisible(false);
        currentOrderLinesList.clear();
        selectedOrder = null;
    }
    
@FXML
private void showCreateForm() {
    try {
        BorderPane root = (BorderPane) salesOrdersTable.getScene().getRoot();
        root.setCenter(FXMLLoader.load(getClass().getResource("/ims/view/CreateSalesOrder.fxml")));
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}