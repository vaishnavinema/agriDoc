
// --- REST Controller: AgriDocController.java (UPDATED) ---
package com.agridoc.controller;

import com.agridoc.entity.DiagnosisRecord;
import com.agridoc.entity.User;
import com.agridoc.repository.DiagnosisRecordRepository;
import com.agridoc.repository.UserRepository;
import com.agridoc.service.DiagnosisService;
import com.agridoc.service.WeatherService;
import com.agridoc.service.gemini.DiagnosisResultJson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AgriDocController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DiagnosisRecordRepository diagnosisRecordRepository;
    @Autowired
    private DiagnosisService diagnosisService;
    @Autowired
    private WeatherService weatherService;
    // Endpoint for user registration (no changes)
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User newUser) {
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already in use");
        }
        User savedUser = userRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // Endpoint to get diagnosis history (no changes)
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<DiagnosisRecord>> getHistory(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        List<DiagnosisRecord> history = diagnosisRecordRepository.findByUserIdOrderByDiagnosedAtDesc(userId);
        return ResponseEntity.ok(history);
    }

    // Endpoint to diagnose an image (UPDATED)
    @PostMapping("/diagnose")
    public ResponseEntity<?> diagnose(
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam("userId") Long userId) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }

        try {
            // 1. Call the updated diagnosis service
            DiagnosisResultJson result = diagnosisService.diagnose(imageFile);

            // For now, we'll use a placeholder image URL. In a real app, you'd upload this to cloud storage.
            String imageUrl = "uploads/" + imageFile.getOriginalFilename();

            // 2. Create and save the record
            DiagnosisRecord record = new DiagnosisRecord();
            record.setUser(user);
            record.setImageUrl(imageUrl);
            record.setDiseaseName(result.diseaseName);
            record.setConfidenceScore(result.confidenceScore);
            record.setTreatmentRecommended(result.treatmentRecommended);

            DiagnosisRecord savedRecord = diagnosisRecordRepository.save(record);

            // Return the saved record, which now has a real AI diagnosis
            return ResponseEntity.ok(savedRecord);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to diagnose image: " + e.getMessage()));
        }
    }

// AgriDocController.java ke andar, dusre methods ke saath isse add karein

    @GetMapping("/forecast")
    public ResponseEntity<?> getForecast() {
        try {
            // We are hardcoding the location to Jabalpur for this example.
            // A more advanced version could get the user's location.
            Object forecast = weatherService.getForecast("Jabalpur");
            return ResponseEntity.ok(forecast);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to get forecast: " + e.getMessage()));
        }
    }
}