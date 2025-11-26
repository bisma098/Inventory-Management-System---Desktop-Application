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
    private Label messageLabel;
    @FXML
private RestoreController restoreController; // inject it or get reference

@FXML
private void handleRestore() {
    Backup selected = backupTable.getSelectionModel().getSelectedItem();
    if (selected != null) {
        boolean ok = BackupUtils.restoreBackup("C:\\\\Users\\\\User\\\\bismaBranch\\\\Inventory-Management-System---Desktop-Application\\\\backups\\\\" + selected.getFileName());
        messageLabel.setText(ok ? "Restore completed!" : "Restore failed!");
        if (ok && restoreController != null) {
            restoreController.addRestoreRecord(selected.getFileName(), "adminUser"); // replace with actual user
        }
    } else {
        messageLabel.setText("Select a backup first!");
    }
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

    private void loadBackupFiles() {
        backupList.clear();
        File folder = new File("backups/");
        if (folder.exists()) {
            for (File file : folder.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".bak")) {
                    backupList.add(new Backup(file.getName(), LocalDateTime.ofEpochSecond(file.lastModified()/1000,0,java.time.ZoneOffset.UTC), "SUCCESS"));
                }
            }
        }
        backupTable.setItems(backupList);
    }

    
}
