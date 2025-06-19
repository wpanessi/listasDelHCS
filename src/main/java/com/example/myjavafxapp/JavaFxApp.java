package com.example.myjavafxapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.URL;

public class JavaFxApp extends Application {

    private static ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        // Initialize Spring Boot application context
        springContext = new SpringApplication(MyJavafxAppApplication.class).run();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Council Management Application");

        try {
            URL fxmlUrl = getClass().getResource("/com/example/myjavafxapp/view/MainAppView.fxml");
            if (fxmlUrl == null) {
                System.err.println("Cannot load FXML file. Make sure the path is correct: /com/example/myjavafxapp/view/MainAppView.fxml");
                Label label = new Label("Error: MainAppView.fxml not found! Check logs.");
                StackPane root = new StackPane(label);
                Scene scene = new Scene(root, 900, 700);
                primaryStage.setScene(scene);
            } else {
                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                // Set Spring context as controller factory
                loader.setControllerFactory(springContext::getBean);
                Parent root = loader.load();
                Scene scene = new Scene(root, 900, 700);
                // Apply CSS
                String cssPath = "/com/example/myjavafxapp/css/styles.css";
                URL cssUrl = getClass().getResource(cssPath);
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                } else {
                    System.err.println("Cannot load CSS file: " + cssPath);
                }
                primaryStage.setScene(scene);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Label label = new Label("Error loading MainAppView.fxml! Check logs.");
            StackPane root = new StackPane(label);
            Scene scene = new Scene(root, 900, 700);
            primaryStage.setScene(scene);
        }

        primaryStage.show();
    }

    @Override
    public void stop() {
        // Close Spring Boot application context
        if (springContext != null) {
            springContext.close();
        }
    }

    // Getter for the Spring context, can be used by controllers
    public static ConfigurableApplicationContext getSpringContext() {
        return springContext;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
