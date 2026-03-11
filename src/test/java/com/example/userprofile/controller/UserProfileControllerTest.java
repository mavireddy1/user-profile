package com.example.userprofile.controller;

import com.example.userprofile.dto.UserProfileRequest;
import com.example.userprofile.dto.UserProfileResponse;
import com.example.userprofile.exception.UserProfileNotFoundException;
import com.example.userprofile.service.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileService userProfileService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserProfileRequest validRequest;
    private UserProfileResponse sampleResponse;

    @BeforeEach
    void setUp() {
        validRequest = UserProfileRequest.builder()
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

        sampleResponse = UserProfileResponse.builder()
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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .build();
    }

    @Test
    void createUserProfile_shouldReturn201OnSuccess() throws Exception {
        when(userProfileService.createUserProfile(eq("user-1"), any(UserProfileRequest.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/api/users/user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value("user-1"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"));
    }

    @Test
    void createUserProfile_shouldReturn400WhenRequestInvalid() throws Exception {
        UserProfileRequest invalidRequest = UserProfileRequest.builder()
                .firstName("")
                .email("not-an-email")
                .build();

        mockMvc.perform(post("/api/users/user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserProfile_shouldReturn200OnSuccess() throws Exception {
        when(userProfileService.getUserProfile("user-1")).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/users/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value("user-1"));
    }

    @Test
    void getUserProfile_shouldReturn404WhenNotFound() throws Exception {
        when(userProfileService.getUserProfile("user-99"))
                .thenThrow(new UserProfileNotFoundException("User profile not found for userId: user-99"));

        mockMvc.perform(get("/api/users/user-99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getAllUserProfiles_shouldReturn200WithList() throws Exception {
        when(userProfileService.getAllUserProfiles()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].userId").value("user-1"));
    }

    @Test
    void updateUserProfile_shouldReturn200OnSuccess() throws Exception {
        when(userProfileService.updateUserProfile(eq("user-1"), any(UserProfileRequest.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(put("/api/users/user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value("user-1"));
    }

    @Test
    void updateUserProfile_shouldReturn404WhenUserNotFound() throws Exception {
        when(userProfileService.updateUserProfile(eq("user-99"), any(UserProfileRequest.class)))
                .thenThrow(new UserProfileNotFoundException("User profile not found for userId: user-99"));

        mockMvc.perform(put("/api/users/user-99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deleteUserProfile_shouldReturn200OnSuccess() throws Exception {
        doNothing().when(userProfileService).deleteUserProfile("user-1");

        mockMvc.perform(delete("/api/users/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteUserProfile_shouldReturn404WhenUserNotFound() throws Exception {
        doThrow(new UserProfileNotFoundException("User profile not found for userId: user-99"))
                .when(userProfileService).deleteUserProfile("user-99");

        mockMvc.perform(delete("/api/users/user-99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
