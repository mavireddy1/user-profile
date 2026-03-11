package com.example.userprofile.service;

import com.example.userprofile.dto.UserProfileRequest;
import com.example.userprofile.dto.UserProfileResponse;

import java.util.List;

public interface UserProfileService {

    UserProfileResponse createUserProfile(String userId, UserProfileRequest request);

    UserProfileResponse updateUserProfile(String userId, UserProfileRequest request);

    void deleteUserProfile(String userId);

    UserProfileResponse getUserProfile(String userId);

    List<UserProfileResponse> getAllUserProfiles();
}
