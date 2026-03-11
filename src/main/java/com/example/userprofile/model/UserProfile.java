package com.example.userprofile.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String profilePictureUrl;
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
}
