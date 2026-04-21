package com.careerai.controller;

import com.careerai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;

@Controller
public class GlobalController {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addUserToSession(Authentication auth, HttpSession session) {

        if (auth != null && auth.isAuthenticated()) {

            String email = auth.getName();

            if (session.getAttribute("user") == null) {
                userRepository.findByEmail(email).ifPresent(user -> {
                    session.setAttribute("user", user);
                });
            }
        }
    }
}