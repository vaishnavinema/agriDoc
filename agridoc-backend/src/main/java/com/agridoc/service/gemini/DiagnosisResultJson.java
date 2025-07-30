// --- Gemini API DTOs: GeminiDtos.java (UPDATED with @JsonIgnoreProperties) ---
// (Located in: com.agridoc.service.gemini)
package com.agridoc.service.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DiagnosisResultJson {
    @JsonProperty("diseaseName")
    public String diseaseName;
    @JsonProperty("confidenceScore")
    public BigDecimal confidenceScore;
    @JsonProperty("treatmentRecommended")
    public String treatmentRecommended;
}
