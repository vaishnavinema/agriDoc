package com.agridoc.service.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerationConfig {
    public String responseMimeType;

    public GenerationConfig() {}

    public GenerationConfig(String responseMimeType) { this.responseMimeType = responseMimeType; }
}
