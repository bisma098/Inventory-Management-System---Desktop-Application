package ims.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        showLoginPage();
    }

    public static void showLoginPage() {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("Login.fxml"));
            Scene scene = new Scene(root, 1200,700);
            primaryStage.setTitle("IMS - Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeScene(String fxmlFile, String title) {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource(fxmlFile));
            Scene scene = new Scene(root, 1200, 700);
            primaryStage.setTitle("IMS - " + title);
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
    try {
        ims.database.DatabaseConnection.getConnection();
    } catch (Exception e) {
        e.printStackTrace();
    }

    launch(args);
}

}