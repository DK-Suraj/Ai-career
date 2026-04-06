package com.careerai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIService {

    @Autowired
    private OpenRouterService openRouterService;

    public String analyzeSkills(List<String> skills) {

        String skillText = String.join(", ", skills);

        String prompt =
                "You are a career expert.\n" +
                "Analyze these skills: " + skillText + "\n\n" +
                "Give:\n" +
                "1. Best career roles\n" +
                "2. Missing skills\n" +
                "3. Learning roadmap\n" +
                "4. Salary prediction";

        return openRouterService.askAI(prompt);
    }
}
