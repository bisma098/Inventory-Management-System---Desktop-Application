package ims.controller;

import ims.model.Backup;
import ims.database.BackupUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupController {

    @FXML
    private TableView<Backup> backupTable;
    @FXML
    private TableColumn<Backup, String> fileNameCol;
    @FXML
    private TableColumn<Backup, String> createdAtCol;
    @FXML
    private TableColumn<Backup, String> statusCol;
    @FXML
    private Button restoreButton;
    @FXML
    private Button backupButton; // Add this line
    @FXML
    private Label messageLabel;
    @FXML
    private RestoreController restoreController;

    // Add this backup handler method
    @FXML
    private void handleBackup() {
        boolean success = BackupUtils.createBackup();
        if (success) {
            messageLabel.setText("Backup created successfully!");
            loadBackupFiles(); // Refresh the table to show new backup
        } else {
            messageLabel.setText("Backup failed!");
        }
    }

    @FXML
    private void handleRestore() {
    Backup selected = backupTable.getSelectionModel().getSelectedItem();
    if (selected != null) {
        boolean ok = BackupUtils.restoreBackup("C:\\SQLBackups\\" + selected.getFileName());
        messageLabel.setText(ok ? "Restore completed!" : "Restore failed!");
        if (ok && restoreController != null) {
            restoreController.addRestoreRecord(selected.getFileName(), "adminUser");
        }
    } else {
        messageLabel.setText("Select a backup first!");
    }
    }

    private void loadBackupFiles() {
    backupList.clear();
    File folder = new File("C:\\SQLBackups\\");
    if (folder.exists()) {
        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".bak")) {
                backupList.add(new Backup(file.getName(), LocalDateTime.ofEpochSecond(file.lastModified()/1000,0,java.time.ZoneOffset.UTC), "SUCCESS"));
            }
        }
    }
    backupTable.setItems(backupList);
    }

    private ObservableList<Backup> backupList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        fileNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFileName()));
        createdAtCol.setCellValueFactory(data -> new SimpleStringProperty(
        data.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        loadBackupFiles();
    }


}