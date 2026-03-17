package com.careerai.controller;

import com.careerai.model.User;
import com.careerai.repository.UserRepository;
import com.careerai.service.EmailService;
import com.careerai.util.OTPGenerator;

import org.springframework.beans.factory.annotation.Autowired;
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

        // Duplicate email check
        if(userRepository.existsByEmail(user.getEmail())){
            model.addAttribute("error", "Email already registered!");
            return "register";
        }

        // Handle file upload
        if(photoFile != null && !photoFile.isEmpty()){
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File dir = new File(uploadDir);
            if(!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + photoFile.getOriginalFilename();
            File saveFile = new File(uploadDir + fileName);
            photoFile.transferTo(saveFile);

            // Save file name to DB
            user.setPhoto(fileName);
        }

        // Generate OTP and mark unverified
        String otp = OTPGenerator.generateOTP(6);
        user.setOtp(otp);
        user.setVerified(false);

        userRepository.save(user);

        // Send verification email
        String subject = "Verify your AI Career Account";
        String message = "Hello " + user.getName() + ",\n\n"
                       + "Your OTP to verify your email is: " + otp + "\n\n"
                       + "Regards,\nAI Career Team";

        emailService.sendEmail(user.getEmail(), subject, message);

        model.addAttribute("success", "Registration successful! OTP sent to your email.");
        model.addAttribute("email", user.getEmail());

        return "verify-otp";
    }


    // ================= VERIFY EMAIL OTP =================
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

        model.addAttribute("success", "Email verified successfully. Please login.");

        return "login";
    }

    // ================= LOGIN USER =================
    @PostMapping("/login-user")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "login";
        }

        User user = optionalUser.get();

        if (!user.isVerified()) {
            model.addAttribute("error", "Please verify your email first.");
            return "login";
        }

        if (!user.getPassword().equals(password)) {
            model.addAttribute("error", "Invalid password");
            return "login";
        }

        session.setAttribute("userId", user.getId());
        session.setAttribute("user", user);

        return "redirect:/career/dashboard";
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

        String subject = "Password Reset OTP";
        String message =
                "Hello " + user.getName() + ",\n\n"
                + "Your OTP to reset password is: " + otp;

        emailService.sendEmail(user.getEmail(), subject, message);

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

        user.setPassword(password);
        user.setOtp(null);

        userRepository.save(user);

        model.addAttribute("success", "Password reset successfully. Please login.");

        return "login";
    }

    // ================= LOGOUT =================
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }

}
