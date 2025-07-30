package com.agridoc.service.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

// Classes to structure the response from the Gemini API
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiResponse {
    public List<Candidate> candidates;
}
