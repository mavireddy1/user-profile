package com.example.userprofile.controller;

import com.example.userprofile.dto.ApiResponse;
import com.example.userprofile.dto.UserProfileRequest;
import com.example.userprofile.dto.UserProfileResponse;
import com.example.userprofile.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing user profile operations.
 * Exposes endpoints for creating, retrieving, updating, and deleting user profiles.
 */
@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * Creates a new user profile.
     *
     * @param userId  the unique identifier for the user (provided by the caller)
     * @param request the user profile details
     * @return the created user profile
     */
    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> createUserProfile(
            @PathVariable String userId,
            @Valid @RequestBody UserProfileRequest request) {
        UserProfileResponse response = userProfileService.createUserProfile(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User profile created successfully", response));
    }

    /**
     * Retrieves a user profile by userId.
     *
     * @param userId the user's ID
     * @return the user profile
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(@PathVariable String userId) {
        UserProfileResponse response = userProfileService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", response));
    }

    /**
     * Retrieves all user profiles.
     *
     * @return list of all user profiles
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserProfileResponse>>> getAllUserProfiles() {
        List<UserProfileResponse> profiles = userProfileService.getAllUserProfiles();
        return ResponseEntity.ok(ApiResponse.success("User profiles retrieved successfully", profiles));
    }

    /**
     * Updates an existing user profile.
     *
     * @param userId  the user's ID
     * @param request the updated profile details
     * @return the updated user profile
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUserProfile(
            @PathVariable String userId,
            @Valid @RequestBody UserProfileRequest request) {
        UserProfileResponse response = userProfileService.updateUserProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("User profile updated successfully", response));
    }

    /**
     * Deletes a user profile by userId.
     *
     * @param userId the user's ID
     * @return a success message
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUserProfile(@PathVariable String userId) {
        userProfileService.deleteUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("User profile deleted successfully"));
    }
}
