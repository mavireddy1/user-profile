package com.example.userprofile.service;

import com.example.userprofile.client.IdentityServiceClient;
import com.example.userprofile.dto.IdentityUserRequest;
import com.example.userprofile.dto.UserProfileRequest;
import com.example.userprofile.dto.UserProfileResponse;
import com.example.userprofile.exception.UserProfileAlreadyExistsException;
import com.example.userprofile.exception.UserProfileNotFoundException;
import com.example.userprofile.model.UserProfile;
import com.example.userprofile.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final IdentityServiceClient identityServiceClient;

    @Autowired
    public UserProfileServiceImpl(UserProfileRepository userProfileRepository,
                                  IdentityServiceClient identityServiceClient) {
        this.userProfileRepository = userProfileRepository;
        this.identityServiceClient = identityServiceClient;
    }

    @Override
    public UserProfileResponse createUserProfile(String userId, UserProfileRequest request) {
        if (userProfileRepository.existsById(userId)) {
            throw new UserProfileAlreadyExistsException(userId, "createUserProfile");
        }
        if (userProfileRepository.existsByEmail(request.getEmail())) {
            throw new UserProfileAlreadyExistsException(
                    "A profile with email " + request.getEmail() + " already exists");
        }

        // Notify Identity Service about the new user
        IdentityUserRequest identityRequest = IdentityUserRequest.builder()
                .userId(userId)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
        identityServiceClient.registerUser(identityRequest);

        UserProfile userProfile = UserProfile.builder()
                .userId(userId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .zipCode(request.getZipCode())
                .profilePictureUrl(request.getProfilePictureUrl())
                .bio(request.getBio())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .build();

        UserProfile saved = userProfileRepository.save(userProfile);
        return mapToResponse(saved);
    }

    @Override
    public UserProfileResponse updateUserProfile(String userId, UserProfileRequest request) {
        UserProfile existing = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException(userId, "updateUserProfile"));

        // Check if email is being changed to one that belongs to another user
        if (!existing.getEmail().equalsIgnoreCase(request.getEmail())
                && userProfileRepository.existsByEmail(request.getEmail())) {
            throw new UserProfileAlreadyExistsException(
                    "A profile with email " + request.getEmail() + " already exists");
        }

        // Notify Identity Service about the update
        IdentityUserRequest identityRequest = IdentityUserRequest.builder()
                .userId(userId)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
        identityServiceClient.updateUser(userId, identityRequest);

        UserProfile updated = UserProfile.builder()
                .userId(userId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .zipCode(request.getZipCode())
                .profilePictureUrl(request.getProfilePictureUrl())
                .bio(request.getBio())
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .active(existing.isActive())
                .build();

        UserProfile saved = userProfileRepository.update(updated);
        return mapToResponse(saved);
    }

    @Override
    public void deleteUserProfile(String userId) {
        if (!userProfileRepository.existsById(userId)) {
            throw new UserProfileNotFoundException(userId, "deleteUserProfile");
        }

        // Notify Identity Service about the deletion
        identityServiceClient.deleteUser(userId);

        userProfileRepository.deleteById(userId);
    }

    @Override
    public UserProfileResponse getUserProfile(String userId) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException(userId, "getUserProfile"));
        return mapToResponse(userProfile);
    }

    @Override
    public List<UserProfileResponse> getAllUserProfiles() {
        return userProfileRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private UserProfileResponse mapToResponse(UserProfile userProfile) {
        return UserProfileResponse.builder()
                .userId(userProfile.getUserId())
                .firstName(userProfile.getFirstName())
                .lastName(userProfile.getLastName())
                .email(userProfile.getEmail())
                .phoneNumber(userProfile.getPhoneNumber())
                .address(userProfile.getAddress())
                .city(userProfile.getCity())
                .state(userProfile.getState())
                .country(userProfile.getCountry())
                .zipCode(userProfile.getZipCode())
                .profilePictureUrl(userProfile.getProfilePictureUrl())
                .bio(userProfile.getBio())
                .createdAt(userProfile.getCreatedAt())
                .updatedAt(userProfile.getUpdatedAt())
                .active(userProfile.isActive())
                .build();
    }
}
