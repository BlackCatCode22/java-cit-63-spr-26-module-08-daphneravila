package com.example.demo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
@Service
public class WeatherService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Helper method to get NWS Data
    private String fetchNwsWeather(String lat, String lon) {
        try {


            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "FCC-Student-App"); // Required by NWS
            HttpEntity<String> entity = new HttpEntity<>(headers);
            //Step 1: Get the forecast URL from the coordinates
            String pointsUrl = "https://api.weather.gov/points/" + lat + "," + lon;
            ResponseEntity<String> pointsResponse = restTemplate.exchange(pointsUrl, HttpMethod.GET, entity, String.class);
            JsonNode pointNode = objectMapper.readTree(pointsResponse.getBody());
            String forecastUrl = pointNode.path("properties").path("forecast").asText();
            //Step 2: Fetch the actual forecast
            ResponseEntity<String> forecastResponse = restTemplate.exchange(forecastUrl, HttpMethod.GET, entity, String.class);
            JsonNode forecastNode = objectMapper.readTree(forecastResponse.getBody());
            JsonNode current = forecastNode.path("properties").path("periods").get(0);
            return current.path("temperature").asText() + " - " + current.path("temperatureUnit").asText() + " - " + current.path("shortForecast").asText();
        } catch (Exception exception) {
            return "Weather data unavailable";
        }
    }

    public String getFresnoWeather() {
        return fetchNwsWeather("36.7468", "-119.7726");
    }

    public String getNewYorkWeather() {
        return fetchNwsWeather("40.7128", "-74.0060");
    }

    public String getLondonWeather() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();

            String url = "https://api.open-meteo.com/v1/forecast?latitude=51.5085&longitude=-0.1257&current_weather=true";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode data = objectMapper.readTree(response.getBody());

            // Grab the temperature
            String temperature = data.path("current_weather").path("temperature").asText();

            // Grab the weather code to determine the condition
            int weatherCode = data.path("current_weather").path("weathercode").asInt();

            // A simple mapping for common Open-Meteo WMO codes
            String condition = "Unknown";
            if (weatherCode == 0) {
                condition = "Clear";
            } else if (weatherCode >= 1 && weatherCode <= 3) {
                condition = "Partly Cloudy";
            } else if (weatherCode >= 51 && weatherCode <= 67) {
                condition = "Rain";
            } else if (weatherCode >= 71 && weatherCode <= 77) {
                condition = "Snow";
            } else if (weatherCode >= 95) {
                condition = "Thunderstorms";
            }

            // Returns something like "15.4 - C - Partly Cloudy"
            return temperature + " - C - " + condition;

        } catch (Exception exception) {
            return "Weather data unavailable";
        }
    }
}
