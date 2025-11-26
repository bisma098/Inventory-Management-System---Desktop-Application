package ims.controller;

import ims.model.Restore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;

public class RestoreController {

    @FXML
    private TableView<Restore> restoreTable;
    @FXML
    private TableColumn<Restore, String> fileNameCol;
    @FXML
    private TableColumn<Restore, String> restoredByCol;
    @FXML
    private TableColumn<Restore, String> restoredAtCol;

    private ObservableList<Restore> restoreList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        fileNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBackupFileName()));
        restoredByCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRestoredBy()));
        restoredAtCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRestoredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

        restoreTable.setItems(restoreList);
    }

    // Call this after restoring a backup
    public void addRestoreRecord(String fileName, String userName) {
        restoreList.add(new Restore(fileName, userName, java.time.LocalDateTime.now()));
    }
}
