package com.hearthealth.heart_health_system;

import com.hearthealth.heart_health_system.ui.MainUIController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApp extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        // Start Spring before the UI appears
        springContext = new SpringApplicationBuilder(HeartHealthSystemApplication.class)
                .headless(false)
                .run();
    }

    @Override
    public void start(Stage primaryStage) {
        // Get the UI controller from Spring and show the main window
        springContext.getBean(MainUIController.class).start(primaryStage);
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }
}