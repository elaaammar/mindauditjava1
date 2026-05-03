package com.example.mindjavafx.util;

import java.util.regex.Pattern;

public class Validation {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private static final Pattern NAME_PATTERN = 
        Pattern.compile("^[a-zA-Zàâäéèêëïîôöùûüœæ\\s'-]{2,}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches() && name.length() >= 2;
    }

    public static boolean isValidAge(int age) {
        return age >= 18 && age <= 100;
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidTelephone(String telephone) {
        return telephone == null || telephone.isEmpty() || telephone.matches("^[0-9\\-\\+\\s()]{8,}$");
    }
}