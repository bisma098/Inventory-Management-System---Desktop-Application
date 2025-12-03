package ims.controller;


import javafx.fxml.FXML;
import ims.model.BatchLot;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import ims.model.Warehouse;
import javafx.scene.control.cell.PropertyValueFactory;

public class WarehouseTrackingController {

    @FXML private TableView<Warehouse> warehouseTable;
    @FXML private TableColumn<Warehouse, Integer> colId;
    @FXML private TableColumn<Warehouse, String> colName;
    @FXML private TableColumn<Warehouse, String> colAddress;
    @FXML private TableView<BatchLot> batchTable;
@FXML private TableColumn<BatchLot, Integer> colBatchId;
@FXML private TableColumn<BatchLot, Integer> colProductId;
@FXML private TableColumn<BatchLot, Integer> colQuantity;
@FXML private TableColumn<BatchLot, String> colExpiry;
@FXML private TableColumn<BatchLot, Integer> colProductName;

    @FXML private Label lblSelectedName;
@FXML private Label lblSelectedAddress;
@FXML private Label lblBatchCount;

    @FXML private TextField txtName;
    @FXML private TextField txtAddress;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("warehouseId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("warehouseName"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colBatchId.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colExpiry.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));

        warehouseTable.getSelectionModel().selectedItemProperty().addListener(
    (obs, oldVal, newVal) -> {
        if (newVal != null) {
            showWarehouseDetails(newVal);
        }
    }
);
    warehouseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadWarehouses();
    }
    
private void showWarehouseDetails(Warehouse warehouse) {

    lblSelectedName.setText("Name: " + warehouse.getWarehouseName());
    lblSelectedAddress.setText("Address: " + warehouse.getAddress());

    // Load batches that belong to this warehouse
    List<BatchLot> list =
            DataController.getInstance().getBatchListByWarehouse(warehouse.getWarehouseId());

    batchTable.setItems(FXCollections.observableArrayList(list));
    batchTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    lblBatchCount.setText("Total Batches: " + list.size());
}


   private void loadWarehouses() {
    warehouseTable.setItems(
        javafx.collections.FXCollections.observableArrayList(
            DataController.getInstance().getWarehouses()
        )
    );
    }


}