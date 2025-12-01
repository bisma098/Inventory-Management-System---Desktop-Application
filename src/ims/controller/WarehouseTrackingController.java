package ims.controller;


import javafx.fxml.FXML;
import ims.model.Backup;
import ims.model.User;

import java.util.List;

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

    @FXML private TextField txtName;
    @FXML private TextField txtAddress;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("warehouseId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("warehouseName"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        loadWarehouses();
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

