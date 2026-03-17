package com.careerai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OpenRouterService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    private final String apiUrl = "https://openrouter.ai/api/v1/chat/completions";

    public String askAI(String question) {

        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", question);

            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(message);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "meta-llama/llama-3-8b-instruct");
            body.put("messages", messages);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(apiUrl, entity, Map.class);

            Map responseBody = response.getBody();

            if(responseBody == null){
                return "AI returned no response.";
            }

            List choices = (List) responseBody.get("choices");

            if(choices == null || choices.isEmpty()){
                return "No AI result found.";
            }

            Map choice = (Map) choices.get(0);

            Map messageResponse = (Map) choice.get("message");

            return messageResponse.get("content").toString();

        } catch (Exception e) {

            e.printStackTrace();

            return "AI connection error.";
        }
    }}
