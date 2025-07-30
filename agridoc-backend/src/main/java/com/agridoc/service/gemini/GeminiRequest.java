package com.agridoc.service.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

// Classes to structure the request to the Gemini API
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiRequest {
    public List<Content> contents;
    public GenerationConfig generationConfig;

    public GeminiRequest() {}

    public GeminiRequest(List<Content> contents, GenerationConfig generationConfig) {
        this.contents = contents;
        this.generationConfig = generationConfig;
    }
}
