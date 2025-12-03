package ims.controller;

import ims.database.DatabaseConnection;
import ims.model.BatchLot;
import ims.model.Product;
import ims.model.Warehouse;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class BatchTrackingController implements Initializable {

    @FXML private TableView<BatchLot> batchTable;

    @FXML private TableColumn<BatchLot, Integer> colBatchId;
    @FXML private TableColumn<BatchLot, String> colProduct;
    @FXML private TableColumn<BatchLot, String> colWarehouse;
    @FXML private TableColumn<BatchLot, Integer> colTotalQty;
    @FXML private TableColumn<BatchLot, Integer> colAvailQty;
    @FXML private TableColumn<BatchLot, LocalDate> colMfg;
    @FXML private TableColumn<BatchLot, LocalDate> colExp;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colBatchId.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));       // uses helper
        colWarehouse.setCellValueFactory(new PropertyValueFactory<>("warehouseName"));   // uses helper
        colTotalQty.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));
        colAvailQty.setCellValueFactory(new PropertyValueFactory<>("availableQuantity"));
        colMfg.setCellValueFactory(new PropertyValueFactory<>("manufactureDate"));
        colExp.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));

        loadBatches();
    }


private void loadBatches() {
    ObservableList<BatchLot> list = FXCollections.observableArrayList();

    // Use in-memory list from DataController
    list.addAll(DataController.getInstance().getBatchLots());

    batchTable.setItems(list);
}


}
