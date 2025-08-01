package com.agridoc.controller;

import com.agridoc.dto.LoginRequest;
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
import java.util.Optional;

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

    // NEW: Real Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // IMPORTANT: In a real app, passwords should be hashed and compared securely.
            // For this project, we are doing a simple text comparison.
            if (user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok(user);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User newUser) {
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already in use");
        }
        // IMPORTANT: In a real app, hash the password before saving.
        User savedUser = userRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<DiagnosisRecord>> getHistory(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        List<DiagnosisRecord> history = diagnosisRecordRepository.findByUserIdOrderByDiagnosedAtDesc(userId);
        return ResponseEntity.ok(history);
    }


@PostMapping("/diagnose")
public ResponseEntity<?> diagnose(
        @RequestParam("image") MultipartFile imageFile,
        @RequestParam("userId") Long userId) {

    User user = userRepository.findById(userId).orElse(null);
    if (user == null) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found")); }

    try {
        DiagnosisResultJson result = diagnosisService.diagnose(imageFile);
        String imageUrl = "uploads/" + imageFile.getOriginalFilename();
        DiagnosisRecord record = new DiagnosisRecord();
        record.setUser(user);
        record.setImageUrl(imageUrl);
        record.setDiseaseName(result.diseaseName);
        record.setConfidenceScore(result.confidenceScore);
        record.setTreatmentRecommended(result.treatmentRecommended);
        DiagnosisRecord savedRecord = diagnosisRecordRepository.save(record);
        return ResponseEntity.ok(savedRecord);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to diagnose image: " + e.getMessage()));
    }
}

@GetMapping("/forecast")
public ResponseEntity<?> getForecast() {
    try {
        Object forecast = weatherService.getForecast("Jabalpur");
        return ResponseEntity.ok(forecast);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to get forecast: " + e.getMessage()));
    }
}
}