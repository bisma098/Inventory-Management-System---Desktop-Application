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

public class SupplierReturnController {
    
    @FXML private TableView<SupplierReturn> supplierReturnsTable;
    @FXML private TableView<SupplierReturnLine> returnLinesTable;
    @FXML private VBox returnDetailsSection;
    
    private DataController dataController;
    private ObservableList<SupplierReturn> supplierReturnsList;
    private ObservableList<SupplierReturnLine> currentReturnLinesList;
    private SupplierReturn selectedReturn;
    
    @FXML
    public void initialize() {
        this.dataController = DataController.getInstance();
        this.supplierReturnsList = FXCollections.observableArrayList();
        this.currentReturnLinesList = FXCollections.observableArrayList();
        
        setupSupplierReturnsTable();
        setupReturnLinesTable();
        loadSupplierReturns();
        
        returnDetailsSection.setVisible(false);
    }
    
    private void setupSupplierReturnsTable() {
        TableColumn<SupplierReturn, Integer> returnIdCol = (TableColumn<SupplierReturn, Integer>) supplierReturnsTable.getColumns().get(0);
        TableColumn<SupplierReturn, String> supplierCol = (TableColumn<SupplierReturn, String>) supplierReturnsTable.getColumns().get(1);
        TableColumn<SupplierReturn, String> dateCol = (TableColumn<SupplierReturn, String>) supplierReturnsTable.getColumns().get(2);
        TableColumn<SupplierReturn, String> reasonCol = (TableColumn<SupplierReturn, String>) supplierReturnsTable.getColumns().get(3);
        TableColumn<SupplierReturn, Double> totalCol = (TableColumn<SupplierReturn, Double>) supplierReturnsTable.getColumns().get(4);
        TableColumn<SupplierReturn, String> actionCol = (TableColumn<SupplierReturn, String>) supplierReturnsTable.getColumns().get(5);
        
        supplierReturnsTable.setItems(supplierReturnsList);
        
        returnIdCol.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        
        supplierCol.setCellValueFactory(cellData -> {
            Supplier supplier = cellData.getValue().getSupplier();
            return new SimpleStringProperty(supplier != null ? supplier.getName() : "Unknown");
        });
        
        dateCol.setCellValueFactory(cellData -> {
            String formattedDate = cellData.getValue().getReturnDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return new SimpleStringProperty(formattedDate);
        });
        
        reasonCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getReason()));
        
        totalCol.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getTotalAmount()).asObject());
        
        // Action column with "View Details" button
        actionCol.setCellValueFactory(cellData -> new SimpleStringProperty("View Details"));
        actionCol.setCellFactory(column -> new TableCell<SupplierReturn, String>() {
            private final Button viewDetailsButton = new Button("View Details");
            {
                viewDetailsButton.setStyle("-fx-background-color: linear-gradient(to bottom, #008080, #006666); -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 4 8;");
                viewDetailsButton.setOnAction(event -> {
                    SupplierReturn returnItem = getTableView().getItems().get(getIndex());
                    showReturnDetails(returnItem);
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
        
        totalCol.setCellFactory(tc -> new TableCell<SupplierReturn, Double>() {
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
        
        supplierReturnsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private void setupReturnLinesTable() {
        TableColumn<SupplierReturnLine, String> productCol = (TableColumn<SupplierReturnLine, String>) returnLinesTable.getColumns().get(0);
        TableColumn<SupplierReturnLine, String> batchCol = (TableColumn<SupplierReturnLine, String>) returnLinesTable.getColumns().get(1);
        TableColumn<SupplierReturnLine, Integer> quantityCol = (TableColumn<SupplierReturnLine, Integer>) returnLinesTable.getColumns().get(2);
        TableColumn<SupplierReturnLine, Double> unitPriceCol = (TableColumn<SupplierReturnLine, Double>) returnLinesTable.getColumns().get(3);
        TableColumn<SupplierReturnLine, Double> lineTotalCol = (TableColumn<SupplierReturnLine, Double>) returnLinesTable.getColumns().get(4);

        productCol.setCellValueFactory(cellData -> {
            Product product = cellData.getValue().getProduct();
            return new SimpleStringProperty(product != null ? product.getName() : "");
        });
        
        batchCol.setCellValueFactory(cellData -> {
            BatchLot batch = cellData.getValue().getBatch();
            if (batch != null) {
                return new SimpleStringProperty("Batch #" + batch.getBatchId());
            } else {
                return new SimpleStringProperty("No Batch");
            }
        });
        
        quantityCol.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        
        unitPriceCol.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getUnitPrice()).asObject());
        
        lineTotalCol.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getTotalPrice()).asObject());
        
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
        
        returnLinesTable.setItems(currentReturnLinesList);
        returnLinesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private void loadSupplierReturns() {
        supplierReturnsList.clear();
        supplierReturnsList.addAll(dataController.getSupplierReturns());
    }
    
    private void showReturnDetails(SupplierReturn returnItem) {
        this.selectedReturn = returnItem;
        
        currentReturnLinesList.clear();
        currentReturnLinesList.addAll(returnItem.getReturnLines());
        
        Label titleLabel = (Label) returnDetailsSection.getChildren().get(0);
        titleLabel.setText("Return Lines for Return ID: " + returnItem.getId());
        
        returnDetailsSection.setVisible(true);
    }
    
    @FXML
    private void hideReturnDetails() {
        returnDetailsSection.setVisible(false);
        currentReturnLinesList.clear();
        selectedReturn = null;
    }
    
    @FXML
    private void showCreateForm() {
        try {
            BorderPane root = (BorderPane) supplierReturnsTable.getScene().getRoot();
            root.setCenter(FXMLLoader.load(getClass().getResource("/ims/view/CreateSupplierReturn.fxml")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}