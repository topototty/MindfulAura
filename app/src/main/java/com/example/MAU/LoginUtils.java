package com.example.MAU;

public class LoginUtils {

    public static boolean areFieldsValid(String email, String password) {
        return email != null && !email.trim().isEmpty() && password != null && !password.trim().isEmpty();
    }
}
