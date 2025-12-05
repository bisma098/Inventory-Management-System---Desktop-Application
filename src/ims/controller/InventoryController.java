// InventoryController.java in controller folder
package ims.controller;

import ims.model.Product;
import ims.model.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;

public class InventoryController {
    
    @FXML private TableView<Product> productsTable;
    @FXML private Label totalProductsLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label outOfStockLabel;
    
    private DataController dataController;
    
    @FXML
    public void initialize() {
        dataController = DataController.getInstance();
        setupTable();
        loadInventoryData();
        updateStats();
    }
    
private void setupTable() {
        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("productId"));
        idCol.setPrefWidth(60);

        TableColumn<Product, String> nameCol = new TableColumn<>("Product Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);

        TableColumn<Product, String> skuCol = new TableColumn<>("SKU");
        skuCol.setCellValueFactory(new PropertyValueFactory<>("SKU"));
        skuCol.setPrefWidth(100);

        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(80);
        priceCol.setCellFactory(tc -> new TableCell<Product, Double>() {
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

        TableColumn<Product, Integer> quantityCol = new TableColumn<>("Qty");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(60);
        quantityCol.setCellFactory(tc -> new TableCell<Product, Integer>() {
            @Override
            protected void updateItem(Integer quantity, boolean empty) {
                super.updateItem(quantity, empty);
                if (empty || quantity == null) {
                    setText(null);
                } else {
                    setText(quantity.toString());
                    if (quantity == 0) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else if (quantity < 10) {
                        setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: green;");
                    }
                }
            }
        });

        TableColumn<Product, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData -> {
            Category cat = cellData.getValue().getCategory();
            return new SimpleStringProperty(cat != null ? cat.getName() : "No Category");
        });
        categoryCol.setPrefWidth(120);

        productsTable.getColumns().addAll(idCol, nameCol, skuCol, priceCol, quantityCol, categoryCol);

        productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private void loadInventoryData() {
        ObservableList<Product> productList = FXCollections.observableArrayList(dataController.getProducts());
        productsTable.setItems(productList);
    }
    
    private void updateStats() {
        int totalProducts = dataController.getProducts().size();
        int lowStock = 0;
        int outOfStock = 0;
        
        for (Product product : dataController.getProducts()) {
            if (product.getQuantity() == 0) {
                outOfStock++;
            } else if (product.getQuantity() < 10) {
                lowStock++;
            }
        }
        
        totalProductsLabel.setText(String.valueOf(totalProducts));
        lowStockLabel.setText(String.valueOf(lowStock));
        outOfStockLabel.setText(String.valueOf(outOfStock));
    }
    
    @FXML
    private void handleRefresh() {
        dataController.loadAllData();
        loadInventoryData();
        updateStats();
    }
    
    
}