package com.careerai.controller;

import com.careerai.service.OpenRouterService;
import com.careerai.util.TextSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for real-time AI chat.
 * Returns clean, sanitized AI responses in plain text.
 */
@RestController
@RequestMapping("/career/ai")
public class AIChatController {

    @Autowired
    private OpenRouterService openRouterService;

    /**
     * Endpoint: POST /career/ai/ask
     * Receives a user question and returns AI response.
     *
     * @param question User input
     * @return AI answer (sanitized)
     */
    @PostMapping("/ask")
    public String askAI(@RequestParam String question) {

        // Check for empty input
        if (question == null || question.isBlank()) {
            return "Please enter a valid question.";
        }

        // Call OpenRouter AI service
        String aiResponse = openRouterService.askAI(question);

        // Sanitize output (removes *, emojis, or unwanted characters)
        return TextSanitizer.sanitize(aiResponse);
    }
}
