package ims.controller;

import ims.database.DatabaseConnection;
import ims.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class AddUserController {

    @FXML private TextField txtUserName;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cbRole;
    @FXML private TextField txtContact;
    @FXML private TextField txtEmail;
    @FXML private TextField txtArea;
    @FXML private TextField txtCity;
    @FXML private TextField txtAddress;

    @FXML
    public void createUser() {
        // Validate all fields first
        if (!validateFields()) {
            return; // Stop if validation fails
        }

        try (Connection conn = DatabaseConnection.getConnection()) {

            // 1️⃣ Insert into ContactInfo
            String contactQuery = "INSERT INTO ContactInfo (contactNumber, email) OUTPUT INSERTED.contactId VALUES (?,?)";
            PreparedStatement psContact = conn.prepareStatement(contactQuery);
            psContact.setString(1, txtContact.getText());
            psContact.setString(2, txtEmail.getText());

            ResultSet rsContact = psContact.executeQuery();
            rsContact.next();
            int contactId = rsContact.getInt(1);

            // 2️⃣ Insert into Address
            String addressQuery = "INSERT INTO Address (area, city, address) OUTPUT INSERTED.addressId VALUES (?,?,?)";
            PreparedStatement psAddress = conn.prepareStatement(addressQuery);
            psAddress.setString(1, txtArea.getText());
            psAddress.setString(2, txtCity.getText());
            psAddress.setString(3, txtAddress.getText());

            ResultSet rsAddress = psAddress.executeQuery();
            rsAddress.next();
            int addressId = rsAddress.getInt(1);

            // 3️⃣ Insert into Users
            String userQuery = "INSERT INTO Users (userName, role, password, contactId, addressId) OUTPUT INSERTED.userId VALUES (?,?,?,?,?)";
            PreparedStatement psUser = conn.prepareStatement(userQuery);
            psUser.setString(1, txtUserName.getText());
            psUser.setString(2, cbRole.getValue());
            psUser.setString(3, txtPassword.getText());
            psUser.setInt(4, contactId);
            psUser.setInt(5, addressId);

            ResultSet rsUser = psUser.executeQuery();
            rsUser.next();
            int newUserId = rsUser.getInt(1);

            // 4️⃣ Insert into their specific role table
            if (cbRole.getValue().equals("Admin")) {
                insertRole(conn, "Admin", newUserId);
            } else if (cbRole.getValue().equals("Manager")) {
                insertRole(conn, "Manager", newUserId);
            } else if (cbRole.getValue().equals("Staff")) {
                insertRole(conn, "Staff", newUserId);
            }

            
            User newUser = new User(
                    newUserId,
                    txtUserName.getText(),
                    txtPassword.getText(),
                    cbRole.getValue()
            );

            DataController.getInstance().addUser(newUser);
DataController dc = DataController.getInstance();
            User currentUser = dc.getCurrentUser();

            if (currentUser != null) {
                dc.logUserActivity(
            currentUser.getUserId(),
            "Created a new user: '" + txtUserName.getText() +
            "' with Role '" + cbRole.getValue() + "'"
            );
        }
            // SUCCESS MESSAGE
            showAlert(Alert.AlertType.INFORMATION, "Success", "User created successfully!");
            
            // Clear form after successful creation
            clearForm();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error while creating user: " + e.getMessage());
        }
    }

    /**
     * Validates all form fields
     * @return true if all fields are valid, false otherwise
     */
    private boolean validateFields() {
        StringBuilder errorMessage = new StringBuilder();
        
        // Check Username
        if (txtUserName.getText() == null || txtUserName.getText().trim().isEmpty()) {
            errorMessage.append("• Username is required\n");
            highlightField(txtUserName, true);
        } else {
            highlightField(txtUserName, false);
        }
        
        // Check Password
        if (txtPassword.getText() == null || txtPassword.getText().trim().isEmpty()) {
            errorMessage.append("• Password is required\n");
            highlightField(txtPassword, true);
        } else if (txtPassword.getText().length() < 4) {
            errorMessage.append("• Password must be at least 4 characters\n");
            highlightField(txtPassword, true);
        } else {
            highlightField(txtPassword, false);
        }
        
        // Check Role
        if (cbRole.getValue() == null || cbRole.getValue().trim().isEmpty()) {
            errorMessage.append("• Role is required\n");
            highlightField(cbRole, true);
        } else {
            highlightField(cbRole, false);
        }
        
        // Check Contact Number
        if (txtContact.getText() == null || txtContact.getText().trim().isEmpty()) {
            errorMessage.append("• Contact number is required\n");
            highlightField(txtContact, true);
        } else if (!txtContact.getText().matches("\\d+")) {
            errorMessage.append("• Contact number must contain only digits\n");
            highlightField(txtContact, true);
        } else {
            highlightField(txtContact, false);
        }
        
        // Check Email
        if (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty()) {
            errorMessage.append("• Email is required\n");
            highlightField(txtEmail, true);
        } else if (!isValidEmail(txtEmail.getText())) {
            errorMessage.append("• Please enter a valid email address\n");
            highlightField(txtEmail, true);
        } else {
            highlightField(txtEmail, false);
        }
        
        // Check Area
        if (txtArea.getText() == null || txtArea.getText().trim().isEmpty()) {
            errorMessage.append("• Area is required\n");
            highlightField(txtArea, true);
        } else {
            highlightField(txtArea, false);
        }
        
        // Check City
        if (txtCity.getText() == null || txtCity.getText().trim().isEmpty()) {
            errorMessage.append("• City is required\n");
            highlightField(txtCity, true);
        } else {
            highlightField(txtCity, false);
        }
        
        // Check Address
        if (txtAddress.getText() == null || txtAddress.getText().trim().isEmpty()) {
            errorMessage.append("• Address is required\n");
            highlightField(txtAddress, true);
        } else {
            highlightField(txtAddress, false);
        }
        
        // If there are validation errors, show them and return false
        if (errorMessage.length() > 0) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Information", 
                     "Please complete the following fields:\n\n" + errorMessage.toString());
            return false;
        }
        
        return true;
    }
    
    /**
     * Highlights fields with red border when invalid, removes highlight when valid
     */
    private void highlightField(Control field, boolean isError) {
        if (isError) {
            field.setStyle("-fx-border-color: #dc2626; -fx-border-width: 2px; -fx-border-radius: 5px;");
        } else {
            field.setStyle("-fx-border-color: #d1d5db; -fx-border-width: 1px; -fx-border-radius: 5px;");
        }
    }
    
    /**
     * Simple email validation
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Clear form after successful submission
     */
    private void clearForm() {
        txtUserName.clear();
        txtPassword.clear();
        txtContact.clear();
        txtEmail.clear();
        txtArea.clear();
        txtCity.clear();
        txtAddress.clear();
        cbRole.getSelectionModel().clearSelection();
        
        // Remove any highlighting
        highlightField(txtUserName, false);
        highlightField(txtPassword, false);
        highlightField(cbRole, false);
        highlightField(txtContact, false);
        highlightField(txtEmail, false);
        highlightField(txtArea, false);
        highlightField(txtCity, false);
        highlightField(txtAddress, false);
    }

    private void insertRole(Connection conn, String roleTable, int userId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO " + roleTable + " (" + roleTable.toLowerCase() + "Id) VALUES (?)"
        );
        ps.setInt(1, userId);
        ps.executeUpdate();
    }
}