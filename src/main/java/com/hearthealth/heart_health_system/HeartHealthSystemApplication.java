package com.hearthealth.heart_health_system;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HeartHealthSystemApplication {
	public static void main(String[] args) {
		// Start JavaFX; Spring is started inside JavaFxApp.init()
		Application.launch(JavaFxApp.class, args);
	}
}