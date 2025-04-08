package com.recruitment.complaint.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class GeoLocationService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${geo.api.url}")
    private String geoApiUrl;

    public GeoLocationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }

    @Cacheable(value = "geolocation", key = "#ip")
    public String getCountryForIp(String ip) {
        // Obsługa lokalnych adresów IP
        if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip) || "::1".equals(ip)) {
            return "Polska";
        }
        String url = UriComponentsBuilder.fromUriString(geoApiUrl)
                .path(ip)
                .queryParam("fields", "country")
                .toUriString();
        try {
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JsonNode json = objectMapper.readTree(response);
            String country = json.path("country").asText("").trim();
            if (country.isEmpty()) {
                log.warn("Nie udało się ustalić kraju dla IP: {}", ip);
                return "Unknown";
            }
            return country;
        } catch (Exception e) {
            log.error("Błąd podczas pobierania geolokalizacji dla IP: {}", ip, e);
        }
        return "Unknown";
    }
}
