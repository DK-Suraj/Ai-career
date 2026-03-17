package com.careerai.controller;

import com.careerai.model.User;
import com.careerai.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository repo;

    // ================= PROFILE PAGE =================
    @GetMapping("/profile")
    public String profilePage(HttpSession session, org.springframework.ui.Model model){

        User user = (User) session.getAttribute("user");

        if(user == null){
            Integer userId = (Integer) session.getAttribute("userId");

            if(userId != null){
                Optional<User> optionalUser = repo.findById(userId);

                if(optionalUser.isPresent()){
                    user = optionalUser.get();
                    session.setAttribute("user", user);
                }
            }
        }

        if(user != null){
            model.addAttribute("user", user);
        }

        return "profile";
    }

    // ================= SAVE PROFILE =================
    @PostMapping("/profile")
    public String saveProfile(

            @RequestParam String phone,
            @RequestParam String skills,
            @RequestParam String education,
            @RequestParam("photoFile") MultipartFile photo,
            HttpSession session

    ) throws IOException {

        User sessionUser = (User) session.getAttribute("user");

        if(sessionUser != null){

            Optional<User> optionalUser = repo.findById(sessionUser.getId());

            if(optionalUser.isPresent()){

                User user = optionalUser.get();

                String uploadDir = System.getProperty("user.dir") + "/uploads/";

                File dir = new File(uploadDir);

                if(!dir.exists()){
                    dir.mkdirs();
                }

                if(photo != null && !photo.isEmpty()){

                    String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();

                    File saveFile = new File(uploadDir + fileName);

                    photo.transferTo(saveFile);

                    user.setPhoto(fileName);
                }

                user.setPhone(phone);
                user.setSkills(skills);
                user.setEducation(education);

                repo.save(user);

                session.setAttribute("user", user);
            }
        }

        return "redirect:/career/dashboard";
    }

}
