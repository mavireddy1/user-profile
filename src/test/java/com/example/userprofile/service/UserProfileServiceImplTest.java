package com.example.userprofile.service;

import com.example.userprofile.client.IdentityServiceClient;
import com.example.userprofile.dto.IdentityUserResponse;
import com.example.userprofile.dto.UserProfileRequest;
import com.example.userprofile.dto.UserProfileResponse;
import com.example.userprofile.exception.UserProfileAlreadyExistsException;
import com.example.userprofile.exception.UserProfileNotFoundException;
import com.example.userprofile.model.UserProfile;
import com.example.userprofile.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private IdentityServiceClient identityServiceClient;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private UserProfileRequest profileRequest;
    private UserProfile existingProfile;

    @BeforeEach
    void setUp() {
        profileRequest = UserProfileRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+12345678901")
                .address("123 Main St")
                .city("Springfield")
                .state("IL")
                .country("US")
                .zipCode("62701")
                .bio("Test user")
                .build();

        existingProfile = UserProfile.builder()
                .userId("user-1")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+12345678901")
                .address("123 Main St")
                .city("Springfield")
                .state("IL")
                .country("US")
                .zipCode("62701")
                .bio("Test user")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .active(true)
                .build();
    }

    @Test
    void createUserProfile_shouldCreateAndReturnProfile() {
        when(userProfileRepository.existsById("user-1")).thenReturn(false);
        when(userProfileRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(identityServiceClient.registerUser(any())).thenReturn(new IdentityUserResponse());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(existingProfile);

        UserProfileResponse response = userProfileService.createUserProfile("user-1", profileRequest);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo("user-1");
        assertThat(response.getEmail()).isEqualTo("john.doe@example.com");
        verify(identityServiceClient).registerUser(any());
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void createUserProfile_shouldThrowWhenUserAlreadyExists() {
        when(userProfileRepository.existsById("user-1")).thenReturn(true);

        assertThatThrownBy(() -> userProfileService.createUserProfile("user-1", profileRequest))
                .isInstanceOf(UserProfileAlreadyExistsException.class);

        verify(identityServiceClient, never()).registerUser(any());
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void createUserProfile_shouldThrowWhenEmailAlreadyExists() {
        when(userProfileRepository.existsById("user-1")).thenReturn(false);
        when(userProfileRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userProfileService.createUserProfile("user-1", profileRequest))
                .isInstanceOf(UserProfileAlreadyExistsException.class);

        verify(identityServiceClient, never()).registerUser(any());
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void updateUserProfile_shouldUpdateAndReturnProfile() {
        UserProfileRequest updateRequest = UserProfileRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("john.doe@example.com")
                .build();

        UserProfile updatedProfile = UserProfile.builder()
                .userId("user-1")
                .firstName("Jane")
                .lastName("Smith")
                .email("john.doe@example.com")
                .createdAt(existingProfile.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .build();

        when(userProfileRepository.findById("user-1")).thenReturn(Optional.of(existingProfile));
        when(identityServiceClient.updateUser(anyString(), any())).thenReturn(new IdentityUserResponse());
        when(userProfileRepository.update(any(UserProfile.class))).thenReturn(updatedProfile);

        UserProfileResponse response = userProfileService.updateUserProfile("user-1", updateRequest);

        assertThat(response).isNotNull();
        assertThat(response.getFirstName()).isEqualTo("Jane");
        verify(identityServiceClient).updateUser(anyString(), any());
        verify(userProfileRepository).update(any(UserProfile.class));
    }

    @Test
    void updateUserProfile_shouldThrowWhenUserNotFound() {
        when(userProfileRepository.findById("user-99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.updateUserProfile("user-99", profileRequest))
                .isInstanceOf(UserProfileNotFoundException.class);

        verify(identityServiceClient, never()).updateUser(anyString(), any());
    }

    @Test
    void deleteUserProfile_shouldDeleteSuccessfully() {
        when(userProfileRepository.existsById("user-1")).thenReturn(true);
        doNothing().when(identityServiceClient).deleteUser("user-1");
        doNothing().when(userProfileRepository).deleteById("user-1");

        userProfileService.deleteUserProfile("user-1");

        verify(identityServiceClient).deleteUser("user-1");
        verify(userProfileRepository).deleteById("user-1");
    }

    @Test
    void deleteUserProfile_shouldThrowWhenUserNotFound() {
        when(userProfileRepository.existsById("user-99")).thenReturn(false);

        assertThatThrownBy(() -> userProfileService.deleteUserProfile("user-99"))
                .isInstanceOf(UserProfileNotFoundException.class);

        verify(identityServiceClient, never()).deleteUser(anyString());
        verify(userProfileRepository, never()).deleteById(anyString());
    }

    @Test
    void getUserProfile_shouldReturnProfile() {
        when(userProfileRepository.findById("user-1")).thenReturn(Optional.of(existingProfile));

        UserProfileResponse response = userProfileService.getUserProfile("user-1");

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo("user-1");
    }

    @Test
    void getUserProfile_shouldThrowWhenNotFound() {
        when(userProfileRepository.findById("user-99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.getUserProfile("user-99"))
                .isInstanceOf(UserProfileNotFoundException.class);
    }

    @Test
    void getAllUserProfiles_shouldReturnAllProfiles() {
        when(userProfileRepository.findAll()).thenReturn(List.of(existingProfile));

        List<UserProfileResponse> profiles = userProfileService.getAllUserProfiles();

        assertThat(profiles).hasSize(1);
        assertThat(profiles.get(0).getUserId()).isEqualTo("user-1");
    }
}
