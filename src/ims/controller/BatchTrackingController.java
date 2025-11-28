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
    @FXML
private void handleAddBatch() {
    // Code to add a new batch (or just a placeholder for now)
    System.out.println("Add Batch clicked");
}

@FXML
private void handleUpdateBatch() {
    System.out.println("Update Batch clicked");
}

@FXML
private void handleDeleteBatch() {
    System.out.println("Delete Batch clicked");
}


    private void loadBatches() {
        ObservableList<BatchLot> list = FXCollections.observableArrayList();

        String query =
    "SELECT b.batchId, b.manufactureDate, b.expiryDate, " +
    "b.totalQuantity, b.availableQty, " +
    "p.product_id, p.product_name, " +
    "w.warehouseId, w.warehouseName " +
    "FROM BatchLot b " +
    "JOIN products p ON b.productId = p.product_id " +
    "JOIN Warehouse w ON b.warehouseId = w.warehouseId";


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
    rs.getInt("product_id"),
    rs.getString("product_name")
);

Warehouse warehouse = new Warehouse(
    rs.getInt("warehouseId"),
    rs.getString("warehouseName")
);


                BatchLot batch = new BatchLot(
    rs.getInt("batchId"),
    rs.getDate("manufactureDate").toLocalDate(),
    rs.getDate("expiryDate").toLocalDate(),
    rs.getInt("totalQuantity"),
    rs.getInt("availableQty"),
    product,
    warehouse
);

                list.add(batch);
            }

            batchTable.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
