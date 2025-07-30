package com.agridoc.service.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Part {
    public String text;
    public InlineData inlineData;

    public Part() {}

    public Part(String text) { this.text = text; }
    public Part(InlineData inlineData) { this.inlineData = inlineData; }
}
