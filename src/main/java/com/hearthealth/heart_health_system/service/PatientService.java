package com.hearthealth.heart_health_system.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/** Handles all file storage for patient info and CT scan results. */
@Service
public class PatientService {

    private static final Path DIR = Paths.get("data"); // all files go in ./data

    private Path infoFile(String id) { return DIR.resolve(id + "_PatientInfo.txt"); }
    private Path ctFile(String id)   { return DIR.resolve(id + "CTResults.txt"); }

    /** Creates a unique 5-digit ID, saves patient info to <id>_PatientInfo.txt, returns the ID. */
    public String createPatient(String first, String last, String email, String phone,
                                String history, String insurance) throws IOException {
        Files.createDirectories(DIR);
        String id;
        do {
            id = String.valueOf(10000 + new Random().nextInt(90000));
        } while (Files.exists(infoFile(id))); // make sure the ID is unique

        String content = "Patient ID: " + id + "\n"
                + "First Name: " + first + "\n"
                + "Last Name: " + last + "\n"
                + "Email: " + email + "\n"
                + "Phone Number: " + phone + "\n"
                + "Health History: " + history.replace("\n", " ") + "\n"
                + "Insurance ID: " + insurance + "\n";
        Files.writeString(infoFile(id), content);
        return id;
    }

    /** True if the ID is 5 digits and a patient file exists for it. */
    public boolean patientExists(String id) {
        return id != null && id.matches("\\d{5}") && Files.exists(infoFile(id));
    }

    /** Reads first and last name from the patient info file. */
    public String getPatientName(String id) throws IOException {
        String first = "", last = "";
        for (String line : Files.readAllLines(infoFile(id))) {
            if (line.startsWith("First Name: ")) first = line.substring(12);
            if (line.startsWith("Last Name: "))  last = line.substring(11);
        }
        return first + " " + last;
    }

    /** Saves CT scores to <id>CTResults.txt. */
    public void saveCtResults(String id, String total, String lm, String lad,
                              String lcx, String rca, String pda) throws IOException {
        Files.createDirectories(DIR);
        String content = "Total: " + total + "\n"
                + "LM: " + lm + "\n"
                + "LAD: " + lad + "\n"
                + "LCX: " + lcx + "\n"
                + "RCA: " + rca + "\n"
                + "PDA: " + pda + "\n";
        Files.writeString(ctFile(id), content);
    }

    public boolean hasCtResults(String id) { return Files.exists(ctFile(id)); }

    /** Reads CT scores into a map (Total, LM, LAD, LCX, RCA, PDA). */
    public Map<String, String> readCtResults(String id) throws IOException {
        Map<String, String> map = new LinkedHashMap<>();
        for (String line : Files.readAllLines(ctFile(id))) {
            int i = line.indexOf(": ");
            if (i > 0) map.put(line.substring(0, i), line.substring(i + 2));
        }
        return map;
    }
}