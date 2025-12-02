package ims.controller;


import javafx.fxml.FXML;
import ims.model.Backup;
import ims.model.BatchLot;
import ims.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Collectors;

import ims.database.DatabaseConnection;

import ims.database.BackupUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import ims.model.Warehouse;
import javafx.scene.control.cell.PropertyValueFactory;
import ims.database.DatabaseConnection;

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
colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
colExpiry.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));

        warehouseTable.getSelectionModel().selectedItemProperty().addListener(
    (obs, oldVal, newVal) -> {
        if (newVal != null) {
            showWarehouseDetails(newVal);
        }
    }
);

        loadWarehouses();
    }
    
private void showWarehouseDetails(Warehouse warehouse) {

    lblSelectedName.setText("Name: " + warehouse.getWarehouseName());
    lblSelectedAddress.setText("Address: " + warehouse.getAddress());

    // Load batches that belong to this warehouse
    List<BatchLot> list =
            DataController.getInstance().getBatchListByWarehouse(warehouse.getWarehouseId());

    batchTable.setItems(FXCollections.observableArrayList(list));

    lblBatchCount.setText("Total Batches: " + list.size());
}


   private void loadWarehouses() {
    warehouseTable.setItems(
        javafx.collections.FXCollections.observableArrayList(
            DataController.getInstance().getWarehouses()
        )
    );
    }


    @FXML
    private void addWarehouse() {
        String name = txtName.getText();
        String address = txtAddress.getText();

        if (name.isEmpty() || address.isEmpty()) return;

        DataController.getInstance().addWarehouse(name, address);
            loadWarehouses();
            txtName.clear();
            txtAddress.clear();
    }
}

