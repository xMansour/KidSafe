package com.mansourappdevelopment.androidapp.kidsafe.utils;

public class Validators {

    public static boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email.equals("")) {
            return false;
        }
        if (!email.trim().matches(emailPattern)) {
            return false;
        }
        return true;
    }

    public static boolean isValidPassword(String password) {
        if (password.equals("")) {
            return false;
        }

        if (password.length() < 6) {
            return false;
        }

        return true;

    }
}
