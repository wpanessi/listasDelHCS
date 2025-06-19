package com.example.myjavafxapp;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyJavafxAppApplication {

    // No static context here; JavaFxApp will manage it.

    public static void main(String[] args) {
        // This main method is primarily for Spring Boot to be recognized as a Spring Boot application.
        // The actual launch sequence will be initiated by JavaFxApp.
        // Application.launch(JavaFxApp.class, args);
        // The above line is removed as JavaFxApp's main method will call launch(args),
        // and its init() method will start the Spring context.
        // MyJavafxAppApplication itself can be run to start Spring context if needed independently,
        // but for JavaFX, JavaFxApp is the entry.
        SpringApplication.run(MyJavafxAppApplication.class, args);
    }
}
