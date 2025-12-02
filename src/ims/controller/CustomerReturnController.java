package ims.controller;

import ims.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.time.format.DateTimeFormatter;

public class CustomerReturnController {

    @FXML private TableView<CustomerReturn> customerReturnTable;
    @FXML private TableView<CustomerReturnLine> returnLinesTable;

    @FXML private VBox returnDetailsSection;
    @FXML private Label customerNameLabel;
    @FXML private Label customerContactLabel;
    @FXML private Label detailsTitle;

    private DataController dataController;

    private ObservableList<CustomerReturn> returnList;
    private ObservableList<CustomerReturnLine> returnLinesList;

    private CustomerReturn selectedReturn;

    @FXML
    public void initialize() {
        dataController = DataController.getInstance();
        returnList = FXCollections.observableArrayList();
        returnLinesList = FXCollections.observableArrayList();

        setupReturnTable();
        setupReturnLinesTable();
        loadCustomerReturns();

        returnDetailsSection.setVisible(false);
        returnDetailsSection.setManaged(false);
    }

    // ------------------------------------------------------------
    // TABLE SETUP (Main Customer Return Table)
    // ------------------------------------------------------------
    private void setupReturnTable() {

        TableColumn<CustomerReturn, Integer> idCol =
                (TableColumn<CustomerReturn, Integer>) customerReturnTable.getColumns().get(0);

        TableColumn<CustomerReturn, String> customerCol =
                (TableColumn<CustomerReturn, String>) customerReturnTable.getColumns().get(1);

        TableColumn<CustomerReturn, String> dateCol =
                (TableColumn<CustomerReturn, String>) customerReturnTable.getColumns().get(2);

        TableColumn<CustomerReturn, String> reasonCol =
                (TableColumn<CustomerReturn, String>) customerReturnTable.getColumns().get(3);

        TableColumn<CustomerReturn, Double> totalCol =
                (TableColumn<CustomerReturn, Double>) customerReturnTable.getColumns().get(4);

        TableColumn<CustomerReturn, Integer> countCol =
                (TableColumn<CustomerReturn, Integer>) customerReturnTable.getColumns().get(5);

        TableColumn<CustomerReturn, String> actionCol =
                (TableColumn<CustomerReturn, String>) customerReturnTable.getColumns().get(6);

        customerReturnTable.setItems(returnList);

        idCol.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getReturnId()).asObject());

        customerCol.setCellValueFactory(c -> {
            Customer cus = c.getValue().getCustomer();
            return new SimpleStringProperty(cus != null ? cus.getName() : "Unknown");
        });

        dateCol.setCellValueFactory(c -> {
            String formatted = c.getValue().getReturnDate()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return new SimpleStringProperty(formatted);
        });

        reasonCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getReason()));

        totalCol.setCellValueFactory(c -> {
            double total = c.getValue().getReturnLines().stream()
                            .mapToDouble(line -> line.getQuantity() * line.getProduct().getPrice())
                            .sum();
            return new SimpleDoubleProperty(total).asObject();
        });

        countCol.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getReturnLines().size()).asObject());

        // Add "View Details" button
        actionCol.setCellValueFactory(c -> new SimpleStringProperty("View"));
        actionCol.setCellFactory(col -> new TableCell<CustomerReturn, String>() {

            private final Button btn = new Button("View Details");

            {
                btn.setStyle("-fx-background-color: linear-gradient(to bottom, #008080, #006666); " +
                             "-fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 4 8;");
                btn.setOnAction(e -> {
                    CustomerReturn cr = getTableView().getItems().get(getIndex());
                    showReturnDetails(cr);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Format currency column
        totalCol.setCellFactory(col -> new TableCell<CustomerReturn, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                setText(empty || amount == null ? "" : String.format("$%.2f", amount));
                if (!empty) setStyle("-fx-font-weight: bold;");
            }
        });

        customerReturnTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ------------------------------------------------------------
    // RETURN LINE TABLE SETUP
    // ------------------------------------------------------------
    private void setupReturnLinesTable() {

        TableColumn<CustomerReturnLine, String> productCol =
                (TableColumn<CustomerReturnLine, String>) returnLinesTable.getColumns().get(0);

        TableColumn<CustomerReturnLine, String> batchCol =
                (TableColumn<CustomerReturnLine, String>) returnLinesTable.getColumns().get(1);

        TableColumn<CustomerReturnLine, Integer> qtyCol =
                (TableColumn<CustomerReturnLine, Integer>) returnLinesTable.getColumns().get(2);

        TableColumn<CustomerReturnLine, Double> priceCol =
                (TableColumn<CustomerReturnLine, Double>) returnLinesTable.getColumns().get(3);

        TableColumn<CustomerReturnLine, Double> totalCol =
                (TableColumn<CustomerReturnLine, Double>) returnLinesTable.getColumns().get(4);

        productCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getProduct().getName()));

        batchCol.setCellValueFactory(c -> {
            BatchLot batch = c.getValue().getBatch();
            return new SimpleStringProperty(batch != null ?
                    "Batch #" + batch.getBatchId() : "No Batch");
        });

        qtyCol.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getQuantity()).asObject());

        priceCol.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getProduct().getPrice()).asObject());

        totalCol.setCellValueFactory(c ->
                new SimpleDoubleProperty(
                        c.getValue().getQuantity() * c.getValue().getProduct().getPrice()
                ).asObject());

        priceCol.setCellFactory(col -> new TableCell<CustomerReturnLine, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? "" : String.format("$%.2f", price));
            }
        });

        totalCol.setCellFactory(col -> new TableCell<CustomerReturnLine, Double>() {
            @Override
            protected void updateItem(Double t, boolean empty) {
                super.updateItem(t, empty);
                setText(empty ? "" : String.format("$%.2f", t));
                if (!empty) setStyle("-fx-font-weight: bold;");
            }
        });

        returnLinesTable.setItems(returnLinesList);
        returnLinesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ------------------------------------------------------------
    // LOAD CUSTOMER RETURNS
    // ------------------------------------------------------------
    private void loadCustomerReturns() {
        returnList.clear();
        returnList.addAll(dataController.getCustomerReturns());
    }

    // ------------------------------------------------------------
    // SHOW DETAILS
    // ------------------------------------------------------------
    private void showReturnDetails(CustomerReturn cr) {
        selectedReturn = cr;

        returnLinesList.clear();
        returnLinesList.addAll(cr.getReturnLines());

        detailsTitle.setText("Details for Return ID: " + cr.getReturnId());

        customerNameLabel.setText("Name: " + cr.getCustomer().getName());
        customerContactLabel.setText("Contact: " + cr.getCustomer().getContactInfo());

        returnDetailsSection.setVisible(true);
        returnDetailsSection.setManaged(true);
    }

    @FXML
    private void hideReturnDetails() {
        returnDetailsSection.setVisible(false);
        returnDetailsSection.setManaged(false);
        returnLinesList.clear();
        selectedReturn = null;
    }

    // ------------------------------------------------------------
    // NAVIGATION
    // ------------------------------------------------------------
    @FXML
    private void showCreateForm() {
        try {
            BorderPane root = (BorderPane) customerReturnTable.getScene().getRoot();
            root.setCenter(FXMLLoader.load(getClass().getResource("/ims/view/CreateCustomerReturn.fxml")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
