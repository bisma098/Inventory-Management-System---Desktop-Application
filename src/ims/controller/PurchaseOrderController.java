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

public class PurchaseOrderController {
    
    @FXML private TableView<PurchaseOrder> purchaseOrdersTable;
    @FXML private TableView<PurchaseOrderLine> orderLinesTable;
    @FXML private VBox orderDetailsSection;
    
    private DataController dataController;
    private ObservableList<PurchaseOrder> purchaseOrdersList;
    private ObservableList<PurchaseOrderLine> currentOrderLinesList;
    private PurchaseOrder selectedOrder;
    
    @FXML
    public void initialize() {
        this.dataController = DataController.getInstance();
        this.purchaseOrdersList = FXCollections.observableArrayList();
        this.currentOrderLinesList = FXCollections.observableArrayList();
        
        setupPurchaseOrdersTable();
        setupOrderLinesTable();
        loadPurchaseOrders();
        
        orderDetailsSection.setVisible(false);
    }
    
    private void setupPurchaseOrdersTable() {
        TableColumn<PurchaseOrder, Integer> orderIdCol = (TableColumn<PurchaseOrder, Integer>) purchaseOrdersTable.getColumns().get(0);
        TableColumn<PurchaseOrder, String> supplierCol = (TableColumn<PurchaseOrder, String>) purchaseOrdersTable.getColumns().get(1);
        TableColumn<PurchaseOrder, String> dateCol = (TableColumn<PurchaseOrder, String>) purchaseOrdersTable.getColumns().get(2);
        TableColumn<PurchaseOrder, Double> totalCol = (TableColumn<PurchaseOrder, Double>) purchaseOrdersTable.getColumns().get(3);
        TableColumn<PurchaseOrder, Integer> itemsCol = (TableColumn<PurchaseOrder, Integer>) purchaseOrdersTable.getColumns().get(4);
        TableColumn<PurchaseOrder, String> actionCol = (TableColumn<PurchaseOrder, String>) purchaseOrdersTable.getColumns().get(5);
        
         purchaseOrdersTable.setItems(purchaseOrdersList);
        orderIdCol.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getOrderId()).asObject());
        
        supplierCol.setCellValueFactory(cellData -> {
            Supplier supplier = cellData.getValue().getSupplier();
            return new SimpleStringProperty(supplier != null ? supplier.getName() : "Unknown");
        });
        
        dateCol.setCellValueFactory(cellData -> {
            String formattedDate = cellData.getValue().getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return new SimpleStringProperty(formattedDate);
        });
        
        totalCol.setCellValueFactory(cellData -> {
            double total = cellData.getValue().getOrderLines().stream()
                    .mapToDouble(PurchaseOrderLine::calculateLineTotal)
                    .sum();
            return new SimpleDoubleProperty(total).asObject();
        });
        
        itemsCol.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getOrderLines().size()).asObject());
     
        actionCol.setCellValueFactory(cellData -> new SimpleStringProperty("View Details"));
        actionCol.setCellFactory(column -> new TableCell<PurchaseOrder, String>() {
            private final Button viewDetailsButton = new Button("View Details");
            {
                viewDetailsButton.setStyle("-fx-background-color: linear-gradient(to bottom, #008080, #006666); -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 4 8;");
                viewDetailsButton.setOnAction(event -> {
                    PurchaseOrder order = getTableView().getItems().get(getIndex());
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
        
        totalCol.setCellFactory(tc -> new TableCell<PurchaseOrder, Double>() {
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
        
        purchaseOrdersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private void setupOrderLinesTable() {
        TableColumn<PurchaseOrderLine, String> productCol = (TableColumn<PurchaseOrderLine, String>) orderLinesTable.getColumns().get(0);
        TableColumn<PurchaseOrderLine, String> warehouseCol = (TableColumn<PurchaseOrderLine, String>) orderLinesTable.getColumns().get(1);
        TableColumn<PurchaseOrderLine, String> batchCol = (TableColumn<PurchaseOrderLine, String>) orderLinesTable.getColumns().get(2); 
        TableColumn<PurchaseOrderLine, Integer> quantityCol = (TableColumn<PurchaseOrderLine, Integer>) orderLinesTable.getColumns().get(3);
        TableColumn<PurchaseOrderLine, Double> unitPriceCol = (TableColumn<PurchaseOrderLine, Double>) orderLinesTable.getColumns().get(4);
        TableColumn<PurchaseOrderLine, Double> lineTotalCol = (TableColumn<PurchaseOrderLine, Double>) orderLinesTable.getColumns().get(5);

        
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
        
       
        batchCol.setCellValueFactory(cellData -> {
            BatchLot batchLot = cellData.getValue().getBatchLot();
            if (batchLot != null) {
                return new SimpleStringProperty("Batch #" + batchLot.getBatchId());
            } else {
                return new SimpleStringProperty("No Batch");
            }
        });
        
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
                    setStyle("-fx-font-weight: bold");
                }
            }
        });
        
        
        orderLinesTable.setItems(currentOrderLinesList);
        orderLinesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private void loadPurchaseOrders() {
        purchaseOrdersList.clear();
        purchaseOrdersList.addAll(dataController.getPurchaseOrders());
      
    }
    
    private void showOrderDetails(PurchaseOrder order) {
        this.selectedOrder = order;
        
        currentOrderLinesList.clear();
        currentOrderLinesList.addAll(order.getOrderLines());
        
        Label titleLabel = (Label) orderDetailsSection.getChildren().get(0);
        titleLabel.setText("Order Lines for Order ID- " + order.getOrderId());
        
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
        BorderPane root = (BorderPane) purchaseOrdersTable.getScene().getRoot();
        root.setCenter(FXMLLoader.load(getClass().getResource("/ims/view/CreatePurchaseOrder.fxml")));
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}