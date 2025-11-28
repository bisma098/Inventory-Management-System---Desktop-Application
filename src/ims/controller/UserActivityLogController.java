package ims.controller;

import ims.model.UserActivityLog;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class UserActivityLogController {

    @FXML
    private TableView<UserActivityLog> logTable;

    @FXML
    private TableColumn<UserActivityLog, Integer> colId;

    @FXML
    private TableColumn<UserActivityLog, Integer> colUserId;

    @FXML
    private TableColumn<UserActivityLog, String> colAction;

    @FXML
    private TableColumn<UserActivityLog, String> colTime;

    @FXML
    public void initialize() {

        // Linking table columns with your model fields
        colId.setCellValueFactory(new PropertyValueFactory<>("logId"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("userAction"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        // Load logs from DataController
        logTable.setItems(
            FXCollections.observableArrayList(
                DataController.getInstance().loadAllUserActivityLogs()
            )
        );
    }
}
