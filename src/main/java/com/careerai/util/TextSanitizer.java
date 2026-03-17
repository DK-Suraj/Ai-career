package com.careerai.util;

public class TextSanitizer {

    // Remove unwanted symbols, emojis, stars, etc.
    public static String sanitize(String text){
        if(text == null) return null;

        // Keep normal letters, numbers, punctuation, and new lines
        return text.replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}\\n]", "").trim();
    }
}
