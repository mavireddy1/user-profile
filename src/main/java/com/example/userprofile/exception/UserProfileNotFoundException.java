package com.example.userprofile.exception;

public class UserProfileNotFoundException extends RuntimeException {

    public UserProfileNotFoundException(String message) {
        super(message);
    }

    public UserProfileNotFoundException(String userId, String context) {
        super("User profile not found for userId: " + userId + " [" + context + "]");
    }
}
