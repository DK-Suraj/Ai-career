package com.careerai.controller;

import com.careerai.model.User;
import com.careerai.repository.UserRepository;
import com.careerai.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/career")
public class CareerController {

    @Autowired
    private CareerService careerService;

    @Autowired
    private AIService aiService;

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private OpenRouterService openRouterService;

    @Autowired
    private UserRepository userRepository;

    // ================= DASHBOARD =================
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails){

        if(userDetails == null){
            return "redirect:/login";
        }

        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email).orElse(null);

        if(user == null){
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        return "career-dashboard";
    }

    // ================= SKILL ANALYZER =================
    @PostMapping("/add-skill")
    public String addSkill(Model model,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails){

        if(userDetails == null){
            model.addAttribute("result","Please login first.");
            return "skills-result";
        }

        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email).orElse(null);

        if(user == null){
            model.addAttribute("result","User not found.");
            return "skills-result";
        }

        String skillText = user.getSkills();

        if(skillText == null || skillText.isBlank()){
            model.addAttribute("result","No skills found.");
            return "skills-result";
        }

        List<String> skills = List.of(skillText.split(","));

        String result = aiService.analyzeSkills(skills);

        model.addAttribute("result", result);

        return "skills-result";
    }

    // ================= RESUME =================
    @PostMapping("/upload-resume")
    public String uploadResume(@RequestParam("file") MultipartFile file, Model model){

        if(file == null || file.isEmpty()){
            model.addAttribute("result","Upload PDF!");
            return "resume-result";
        }

        String result = resumeService.analyzeResume(file);
        model.addAttribute("result", result);

        return "resume-result";
    }

    // ================= AI =================
    @PostMapping("/ask-ai")
    public String askAI(@RequestParam String question, Model model){

        String result = openRouterService.askAI(question);
        model.addAttribute("result", result);

        return "result";
    }
}