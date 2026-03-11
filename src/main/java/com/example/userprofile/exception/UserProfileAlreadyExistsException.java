package com.example.userprofile.exception;

public class UserProfileAlreadyExistsException extends RuntimeException {

    public UserProfileAlreadyExistsException(String message) {
        super(message);
    }

    public UserProfileAlreadyExistsException(String userId, String context) {
        super("User profile already exists for userId: " + userId + " [" + context + "]");
    }
}
