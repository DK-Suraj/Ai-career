package com.careerai.controller;

import com.careerai.model.User;
import com.careerai.model.Skill;
import com.careerai.service.AIService;
import com.careerai.service.CareerService;
import com.careerai.service.ResumeService;
import com.careerai.service.OpenRouterService;
import com.careerai.repository.UserRepository;
import com.careerai.util.TextSanitizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
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

    // ---------------- DASHBOARD ----------------
    @GetMapping({"/", "/dashboard"})
    public String dashboard(HttpSession session, Model model){

        User user = (User) session.getAttribute("user");

        if(user == null){
            Integer userId = (Integer) session.getAttribute("userId");

            if(userId != null){
                Optional<User> optionalUser = userRepository.findById(userId);

                if(optionalUser.isPresent()){
                    user = optionalUser.get();
                    session.setAttribute("user", user);
                }
            }
        }

        if(user != null){
            model.addAttribute("name", user.getName());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("phone", user.getPhone());
            model.addAttribute("skills", user.getSkills());
            model.addAttribute("education", user.getEducation());
            model.addAttribute("photo", user.getPhoto());
        }

        return "career-dashboard";
    }

    // ---------------- SKILL ANALYZER ----------------
    @PostMapping("/add-skill")
    public String addSkill(HttpSession session, Model model){

        Integer userId = (Integer) session.getAttribute("userId");

        if(userId == null){
            model.addAttribute("result","Please login first.");
            return "skills-result";
        }

        Optional<User> optionalUser = userRepository.findById(userId);

        if(optionalUser.isEmpty()){
            model.addAttribute("result","User not found.");
            return "skills-result";
        }

        User user = optionalUser.get();

        String skillText = user.getSkills();

        if(skillText == null || skillText.isBlank()){
            model.addAttribute("result","No skills found in profile.");
            return "skills-result";
        }

        List<String> skills = List.of(skillText.split(","));

        String result = aiService.analyzeSkills(skills);

        model.addAttribute("result", TextSanitizer.sanitize(result));

        return "skills-result";
    }

    // ---------------- RESUME ANALYZER ----------------
    @PostMapping("/upload-resume")
    public String uploadResume(@RequestParam("file") MultipartFile file, Model model){

        if(file == null || file.isEmpty()){
            model.addAttribute("result","Please upload a valid PDF resume!");
            return "resume-result";
        }

        String result = resumeService.analyzeResume(file);

        model.addAttribute("result", TextSanitizer.sanitize(result));

        return "resume-result";
    }

    // ---------------- AI ASSISTANT ----------------
    @PostMapping("/ask-ai")
    public String askAI(@RequestParam String question, Model model){

        if(question == null || question.isBlank()){
            model.addAttribute("result","Please enter a valid question.");
            return "result";
        }

        String result = openRouterService.askAI(question);

        model.addAttribute("result", TextSanitizer.sanitize(result));

        return "result";
    }
}
