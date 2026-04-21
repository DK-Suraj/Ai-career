package com.careerai.controller;

import com.careerai.model.User;
import com.careerai.repository.UserRepository;
import com.careerai.service.EmailService;
import com.careerai.util.OTPGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ================= LOGIN PAGE =================
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    // ================= REGISTER PAGE =================
    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }



    // ================= REGISTER USER =================
    @PostMapping("/register-user")
    public String registerUser(@ModelAttribute User user,
                               @RequestParam("photoFile") MultipartFile photoFile,
                               Model model) throws IOException {

        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email already registered!");
            return "register";
        }

        // File upload
        if (photoFile != null && !photoFile.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + photoFile.getOriginalFilename();
            File saveFile = new File(uploadDir + fileName);
            photoFile.transferTo(saveFile);

            user.setPhoto(fileName);
        }

        // 🔥 Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // OTP
        String otp = OTPGenerator.generateOTP(6);
        user.setOtp(otp);
        user.setVerified(false);

        userRepository.save(user);

        // Email
        emailService.sendEmail(
                user.getEmail(),
                "Verify Account",
                "Hello " + user.getName() + ", OTP: " + otp
        );

        model.addAttribute("email", user.getEmail());
        return "verify-otp";
    }

    // ================= VERIFY OTP =================
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email,
                            @RequestParam String otp,
                            Model model) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "verify-otp";
        }

        User user = optionalUser.get();

        if (!otp.equals(user.getOtp())) {
            model.addAttribute("error", "Invalid OTP");
            model.addAttribute("email", email);
            return "verify-otp";
        }

        user.setVerified(true);
        user.setOtp(null);
        userRepository.save(user);

        return "login";
    }

   

    // ================= FORGOT PASSWORD =================
    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String sendResetOtp(@RequestParam String email, Model model) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "Email not found");
            return "forgot-password";
        }

        User user = optionalUser.get();

        String otp = OTPGenerator.generateOTP(6);
        user.setOtp(otp);
        userRepository.save(user);

        emailService.sendEmail(email, "Reset OTP", "OTP: " + otp);

        model.addAttribute("email", email);
        return "verify-reset-otp";
    }

    // ================= VERIFY RESET OTP =================
    @PostMapping("/verify-reset-otp")
    public String verifyResetOtp(@RequestParam String email,
                                 @RequestParam String otp,
                                 Model model) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "verify-reset-otp";
        }

        User user = optionalUser.get();

        if (!otp.equals(user.getOtp())) {
            model.addAttribute("error", "Invalid OTP");
            model.addAttribute("email", email);
            return "verify-reset-otp";
        }

        model.addAttribute("email", email);
        return "reset-password";
    }

    // ================= RESET PASSWORD =================
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email,
                                @RequestParam String password,
                                Model model) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "reset-password";
        }

        User user = optionalUser.get();

        user.setPassword(passwordEncoder.encode(password));
        user.setOtp(null);
        userRepository.save(user);

        return "login";
    }

    // ================= LOGOUT =================
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();
        return "redirect:/login";
    }
}