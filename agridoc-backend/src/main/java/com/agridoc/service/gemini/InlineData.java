package com.agridoc.service.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InlineData {
    public String mimeType;
    public String data;

    public InlineData() {}

    public InlineData(String mimeType, String data) {
        this.mimeType = mimeType;
        this.data = data;
    }
}
