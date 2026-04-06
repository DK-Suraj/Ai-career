package com.careerai.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PhoneOTPService {

    private static final String DIGITS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generate numeric OTP
     * @param length length of OTP
     * @return OTP string
     */
    public String generateOTP(int length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            otp.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        }
        return otp.toString();
    }

    /**
     * Send OTP to phone number
     * Replace this with real SMS API integration (Twilio, MSG91, etc.)
     *
     * @param phoneNumber phone number with country code (+91...)
     * @param otp the OTP to send
     */
    public void sendOTP(String phoneNumber, String otp) {
        // For demo: just print to console
        System.out.println("Sending OTP " + otp + " to phone " + phoneNumber);

        // Example: Twilio integration
        // Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        // Message.creator(new PhoneNumber(phoneNumber), new PhoneNumber(TWILIO_PHONE), "Your OTP is: " + otp).create();
    }
}
