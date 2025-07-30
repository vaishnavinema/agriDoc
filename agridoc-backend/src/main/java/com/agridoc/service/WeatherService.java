package com.agridoc.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

// --- DTOs for Weather and Forecast Response ---

@JsonIgnoreProperties(ignoreUnknown = true)
class WeatherData {
    public String condition;
    public double temperature;
    public int humidity;
    public double windSpeed;
}

@JsonIgnoreProperties(ignoreUnknown = true)
class RiskAssessment {
    public String diseaseName;
    public String riskLevel; // e.g., "Low", "Medium", "High"
}

@JsonIgnoreProperties(ignoreUnknown = true)
class ForecastResponse {
    public WeatherData weather;
    public List<RiskAssessment> risks;
}


@Service
public class WeatherService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openweathermap.api.key}")
    private String weatherApiKey;

    @Autowired
    public WeatherService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public ForecastResponse getForecast(String city) throws IOException {
        // Step 1: Get live weather data from OpenWeatherMap
        WeatherData weatherData = getWeatherData(city);

        // Step 2: Use the weather data to get an AI-powered risk assessment from Gemini
        // (For now, we will simulate this part to avoid another API call)
        List<RiskAssessment> risks = getSimulatedRiskAssessment(weatherData);

        // Step 3: Combine them into a single response
        ForecastResponse forecast = new ForecastResponse();
        forecast.weather = weatherData;
        forecast.risks = risks;
        return forecast;
    }

    private WeatherData getWeatherData(String city) throws IOException {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
                city, weatherApiKey
        );

        try {
            String responseString = webClient.get().uri(url).retrieve().bodyToMono(String.class).block();
            JsonNode root = objectMapper.readTree(responseString);

            WeatherData data = new WeatherData();
            data.condition = root.path("weather").get(0).path("main").asText(); // e.g., "Clouds", "Clear"
            data.temperature = root.path("main").path("temp").asDouble();
            data.humidity = root.path("main").path("humidity").asInt();
            data.windSpeed = root.path("wind").path("speed").asDouble() * 3.6; // Convert m/s to km/h

            return data;
        } catch (Exception e) {
            System.err.println("Error fetching weather data: " + e.getMessage());
            throw new IOException("Could not fetch weather data for " + city, e);
        }
    }

    private List<RiskAssessment> getSimulatedRiskAssessment(WeatherData weather) {
        // This is a simplified logic. A real implementation would call Gemini API
        // with the weather data to get a more nuanced risk assessment.

        RiskAssessment blightRisk = new RiskAssessment();
        blightRisk.diseaseName = "Tomato Late Blight";
        // High risk if humidity is high and temperature is moderate
        if (weather.humidity > 75 && weather.temperature > 18 && weather.temperature < 25) {
            blightRisk.riskLevel = "High";
        } else {
            blightRisk.riskLevel = "Low";
        }

        RiskAssessment rustRisk = new RiskAssessment();
        rustRisk.diseaseName = "Corn Common Rust";
        // Medium risk in warm and humid conditions
        if (weather.humidity > 60 && weather.temperature > 20) {
            rustRisk.riskLevel = "Medium";
        } else {
            rustRisk.riskLevel = "Low";
        }

        RiskAssessment mildewRisk = new RiskAssessment();
        mildewRisk.diseaseName = "Powdery Mildew";
        mildewRisk.riskLevel = "Low"; // Generally lower risk in open fields

        return List.of(blightRisk, rustRisk, mildewRisk);
    }
}
