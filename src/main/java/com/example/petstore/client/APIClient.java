package com.example.petstore.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class APIClient {
    private static final Logger log = LoggerFactory.getLogger(APIClient.class);
    private final HttpClient client;
    private final String baseUrl;
    private final ObjectMapper mapper = new ObjectMapper();

    public APIClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public HttpResponse<String> getInventory(String endpoint) throws IOException, InterruptedException {
        String url = baseUrl + endpoint;
        log.info("GET {}", url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("<= {} {}", response.statusCode(), response.body());
        return response;
    }

    public HttpResponse<String> getOrder(String endpoint, long orderId) throws IOException, InterruptedException {
        String url = baseUrl + endpoint + orderId;
        log.info("GET {}", url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("<= {} {}", response.statusCode(), response.body());
        return response;
    }

    public HttpResponse<String> post(String endpoint, Object body) throws IOException, InterruptedException {
        String url = baseUrl + endpoint;
        String json = body instanceof String ? (String) body : mapper.writeValueAsString(body);
        log.info("POST {} with body: {}", url, json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("<= {} {}", response.statusCode(), response.body());
        return response;
    }

    public HttpResponse<String> delete(String endpoint) throws IOException, InterruptedException {
        String url = baseUrl + endpoint;
        log.info("DELETE {}", url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("<= {} {}", response.statusCode(), response.body());
        return response;
    }
}
