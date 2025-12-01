package ims.controller;

import ims.model.InventoryAuditLog;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class InventoryAuditLogController {

    @FXML
    private TableView<InventoryAuditLog> auditTable;

    @FXML
    private TableColumn<InventoryAuditLog, Integer> colId;

    @FXML
    private TableColumn<InventoryAuditLog, Integer> colUserId;

    @FXML
    private TableColumn<InventoryAuditLog, Integer> colProductId;

    @FXML
    private TableColumn<InventoryAuditLog, String> colDescription;

    @FXML
    private TableColumn<InventoryAuditLog, String> colTimestamp;

    @FXML
    public void initialize() {

        // Link table columns to InventoryAuditLog fields
        colId.setCellValueFactory(new PropertyValueFactory<>("logId"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        // Load all audit logs from DataController
        auditTable.setItems(
            FXCollections.observableArrayList(
                DataController.getInstance().getInventoryAuditLogs()
            )
        );

        auditTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}
