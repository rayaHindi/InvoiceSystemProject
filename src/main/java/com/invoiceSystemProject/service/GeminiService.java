package com.invoiceSystemProject.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class GeminiService {
	private WebClient webClient;
	@Value("${gemini.apiKey}")
	private String apiKey;
	
	public GeminiService(WebClient.Builder builder) {
		this.webClient= builder.baseUrl("https://generativelanguage.googleapis.com/v1beta").build();
		
	}
	
	public Mono<String> generateContent(String userPrompt) {
        String url = "/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        // Create JSON request body
        Map<String, Object> requestBody = Map.of(
            "contents", new Object[]{
                Map.of("parts", new Object[]{ Map.of("text", userPrompt) })
            }
        );

        return webClient.post()
        		.uri(url).
        		bodyValue(requestBody).
        		retrieve().
        		bodyToMono(Map.class)
        		.map(
        				response -> {
        		            // Extract the AI-generated text
        		            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        		            if (candidates != null && !candidates.isEmpty()) {
        		                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        		                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        		                if (parts != null && !parts.isEmpty()) {
        		                    return (String) parts.get(0).get("text");
        		                }
        		            }
        		            return "No response from AI";
        		        });
        }
}
