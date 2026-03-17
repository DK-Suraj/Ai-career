package com.careerai.util;

import java.security.SecureRandom;

public class OTPGenerator {

    private static final String DIGITS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generate a numeric OTP of given length.
     * Example: 6-digit OTP for registration or password reset.
     * 
     * @param length length of the OTP
     * @return generated OTP as String
     */
    public static String generateOTP(int length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            otp.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        }
        return otp.toString();
    }
}
