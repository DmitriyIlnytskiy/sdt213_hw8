package com.hearthealth.heart_health_system.ui;

import com.hearthealth.heart_health_system.service.PatientService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.Map;

/** Builds all JavaFX screens in code (no Scene Builder / FXML). */
@Component
public class MainUIController {

    private final PatientService service;
    private Stage stage;

    public MainUIController(PatientService service) {
        this.service = service;
    }

    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        stage.setTitle("Heart Health Imaging and Recording System");
        showMainView();
        stage.show();
    }

    // ---------- Main view ----------
    private void showMainView() {
        Label title = new Label("Welcome to Heart Health Imaging and Recording System");
        Button intake = bigButton("Patient Intake");
        Button tech = bigButton("CT Scan Tech View");
        Button patient = bigButton("Patient View");
        intake.setOnAction(e -> showIntakeView());
        tech.setOnAction(e -> showTechView());
        patient.setOnAction(e -> showPatientView());

        VBox root = new VBox(20, title, intake, tech, patient);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        stage.setScene(new Scene(root, 500, 400));
    }

    // ---------- Screen 1: Patient Intake ----------
    private void showIntakeView() {
        TextField first = new TextField();
        TextField last = new TextField();
        TextField email = new TextField();
        TextField phone = new TextField();
        TextField history = new TextField();
        TextField insurance = new TextField();
        Label message = new Label();

        GridPane grid = newGrid();
        addRow(grid, 0, "First Name:", first);
        addRow(grid, 1, "Last Name:", last);
        addRow(grid, 2, "Email:", email);
        addRow(grid, 3, "Phone Number:", phone);
        addRow(grid, 4, "Health History:", history);
        addRow(grid, 5, "Insurance ID:", insurance);

        Button save = new Button("Save");
        save.setOnAction(e -> {
            // simple validation: required fields must not be empty
            if (first.getText().isBlank() || last.getText().isBlank()) {
                message.setText("First and last name are required.");
                return;
            }
            try {
                String id = service.createPatient(first.getText(), last.getText(), email.getText(),
                        phone.getText(), history.getText(), insurance.getText());
                message.setText("Saved. Patient ID: " + id);
            } catch (Exception ex) {
                message.setText("Error saving patient: " + ex.getMessage());
            }
        });

        stage.setScene(new Scene(wrap("Patient Intake Form", grid, save, message), 500, 480));
    }

    // ---------- Screen 2: Technician view ----------
    private void showTechView() {
        TextField id = new TextField();
        TextField total = new TextField();
        TextField lm = new TextField();
        TextField lad = new TextField();
        TextField lcx = new TextField();
        TextField rca = new TextField();
        TextField pda = new TextField();
        Label message = new Label();

        GridPane grid = newGrid();
        addRow(grid, 0, "Patient ID:", id);
        addRow(grid, 1, "The total Agatston CAC score:", total);
        grid.add(new Label("Vessel level Agatston CAC score"), 0, 2, 2, 1);
        addRow(grid, 3, "LM:", lm);
        addRow(grid, 4, "LAD:", lad);
        addRow(grid, 5, "LCX:", lcx);
        addRow(grid, 6, "RCA:", rca);
        addRow(grid, 7, "PDA:", pda);

        Button save = new Button("Save");
        save.setOnAction(e -> {
            String pid = id.getText().trim();
            if (!service.patientExists(pid)) {
                message.setText("Wrong patient ID");
                return;
            }
            try {
                service.saveCtResults(pid, total.getText(), lm.getText(), lad.getText(),
                        lcx.getText(), rca.getText(), pda.getText());
                message.setText("CT results saved for patient " + pid);
            } catch (Exception ex) {
                message.setText("Error saving results: " + ex.getMessage());
            }
        });

        stage.setScene(new Scene(wrap("CT Scan Technician View", grid, save, message), 520, 560));
    }

    // ---------- Screen 3: Patient view ----------
    private void showPatientView() {
        TextField id = new TextField();
        Label hello = new Label();
        TextField total = readOnlyField();
        TextField lm = readOnlyField();
        TextField lad = readOnlyField();
        TextField lcx = readOnlyField();
        TextField rca = readOnlyField();
        TextField pda = readOnlyField();
        Label message = new Label();

        GridPane grid = newGrid();
        addRow(grid, 0, "The total Agatston CAC score:", total);
        addRow(grid, 1, "LM:", lm);
        addRow(grid, 2, "LAD:", lad);
        addRow(grid, 3, "LCX:", lcx);
        addRow(grid, 4, "RCA:", rca);
        addRow(grid, 5, "PDA:", pda);

        Button view = new Button("View Results");
        view.setOnAction(e -> {
            // clear previous results
            hello.setText("");
            for (TextField f : new TextField[]{total, lm, lad, lcx, rca, pda}) f.clear();

            String pid = id.getText().trim();
            if (!service.patientExists(pid)) {
                message.setText("Wrong patient ID");
                return;
            }
            try {
                hello.setText("Hello " + service.getPatientName(pid));
                if (!service.hasCtResults(pid)) {
                    message.setText("No data is available yet");
                    return;
                }
                Map<String, String> r = service.readCtResults(pid);
                total.setText(r.getOrDefault("Total", ""));
                lm.setText(r.getOrDefault("LM", ""));
                lad.setText(r.getOrDefault("LAD", ""));
                lcx.setText(r.getOrDefault("LCX", ""));
                rca.setText(r.getOrDefault("RCA", ""));
                pda.setText(r.getOrDefault("PDA", ""));
                message.setText("");
            } catch (Exception ex) {
                message.setText("Error reading data: " + ex.getMessage());
            }
        });

        HBoxLike idRow = new HBoxLike(id);
        stage.setScene(new Scene(wrap("Patient View", idRow.row(), view, hello, grid, message), 520, 540));
    }

    // ---------- Helpers ----------
    private Button bigButton(String text) {
        Button b = new Button(text);
        b.setPrefSize(200, 50);
        return b;
    }

    private GridPane newGrid() {
        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(10);
        return g;
    }

    private void addRow(GridPane g, int row, String label, TextField field) {
        g.add(new Label(label), 0, row);
        g.add(field, 1, row);
    }

    private TextField readOnlyField() {
        TextField f = new TextField();
        f.setEditable(false);
        return f;
    }

    /** Wraps content with a title and a Back button to the main view. */
    private VBox wrap(String title, javafx.scene.Node... nodes) {
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Button back = new Button("Back");
        back.setOnAction(e -> showMainView());
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getChildren().add(t);
        root.getChildren().addAll(nodes);
        root.getChildren().add(back);
        return root;
    }

    /** Small helper: a row with "Patient ID:" label and a field. */
    private static class HBoxLike {
        private final TextField field;
        HBoxLike(TextField field) { this.field = field; }
        javafx.scene.layout.HBox row() {
            return new javafx.scene.layout.HBox(10, new Label("Patient ID:"), field);
        }
    }
}