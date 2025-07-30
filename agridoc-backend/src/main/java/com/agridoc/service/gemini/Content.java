package com.agridoc.service.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Content {
    public List<Part> parts;

    public Content() {}

    public Content(List<Part> parts) { this.parts = parts; }
}
