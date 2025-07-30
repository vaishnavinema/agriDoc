
package com.agridoc.service;

import com.agridoc.service.gemini.*; // Import DTOs
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;

@Service
public class DiagnosisService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Autowired
    public DiagnosisService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Runs the AI diagnosis by calling the Gemini API.
     */
    public DiagnosisResultJson diagnose(MultipartFile imageFile) throws IOException {

        String base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());
        String mimeType = imageFile.getContentType();

        String prompt = "You are an expert in plant pathology. Analyze the attached image of a plant leaf. " +
                "Identify the disease, if any. Provide a confidence score for your diagnosis and a " +
                "step-by-step treatment plan. Respond with ONLY a JSON object in the following format: " +
                "{\"diseaseName\": \"string\", \"confidenceScore\": number (e.g., 95.4), \"treatmentRecommended\": \"string with newline characters\"}. " +
                "If the image is not a plant leaf or no disease is detected, set diseaseName to 'Healthy' or 'Unknown' and confidence to 100.";

        Part textPart = new Part(prompt);
        Part imagePart = new Part(new InlineData(mimeType, base64Image));
        Content content = new Content(List.of(textPart, imagePart));
        GenerationConfig config = new GenerationConfig("application/json");
        GeminiRequest requestPayload = new GeminiRequest(List.of(content), config);

        try {
            String responseString = webClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .bodyValue(requestPayload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // For debugging, let's print the raw response from the API
            System.out.println("Raw AI Response: " + responseString);

            GeminiResponse geminiResponse = objectMapper.readValue(responseString, GeminiResponse.class);

            if (geminiResponse != null && geminiResponse.candidates != null && !geminiResponse.candidates.isEmpty()) {
                String resultJsonText = geminiResponse.candidates.get(0).content.parts.get(0).text;
                resultJsonText = resultJsonText.replace("```json", "").replace("```", "").trim();
                return objectMapper.readValue(resultJsonText, DiagnosisResultJson.class);
            } else {
                throw new IOException("Failed to get a valid response from Gemini API. Response was empty or had no candidates.");
            }

        } catch (Exception e) {
            System.err.println("Error calling or parsing Gemini API: " + e.getMessage());
            throw new IOException("Error during AI diagnosis.", e);
        }
    }
}


