package com.example.userprofile.client;

import com.example.userprofile.dto.IdentityUserRequest;
import com.example.userprofile.dto.IdentityUserResponse;
import com.example.userprofile.exception.IdentityServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Client for making downstream calls to the Identity Service.
 * Handles user registration, update, and deletion in the identity system.
 */
@Component
public class IdentityServiceClient {

    private final RestTemplate restTemplate;

    @Value("${identity.service.base-url}")
    private String identityServiceBaseUrl;

    @Autowired
    public IdentityServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Registers a new user in the Identity Service.
     *
     * @param request the user details to register
     * @return IdentityUserResponse with the result
     */
    public IdentityUserResponse registerUser(IdentityUserRequest request) {
        String url = identityServiceBaseUrl + "/api/identity/users";
        try {
            ResponseEntity<IdentityUserResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, buildHttpEntity(request), IdentityUserResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new IdentityServiceException(
                    "Failed to register user in Identity Service: " + ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            throw new IdentityServiceException(
                    "Identity Service is unavailable: " + ex.getMessage(), ex);
        }
    }

    /**
     * Updates an existing user in the Identity Service.
     *
     * @param userId  the ID of the user to update
     * @param request the updated user details
     * @return IdentityUserResponse with the result
     */
    public IdentityUserResponse updateUser(String userId, IdentityUserRequest request) {
        String url = identityServiceBaseUrl + "/api/identity/users/" + userId;
        try {
            ResponseEntity<IdentityUserResponse> response = restTemplate.exchange(
                    url, HttpMethod.PUT, buildHttpEntity(request), IdentityUserResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new IdentityServiceException(
                    "Failed to update user in Identity Service: " + ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            throw new IdentityServiceException(
                    "Identity Service is unavailable: " + ex.getMessage(), ex);
        }
    }

    /**
     * Deletes a user from the Identity Service.
     *
     * @param userId the ID of the user to delete
     */
    public void deleteUser(String userId) {
        String url = identityServiceBaseUrl + "/api/identity/users/" + userId;
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, buildHttpEntity(null), Void.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new IdentityServiceException(
                    "Failed to delete user in Identity Service: " + ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            throw new IdentityServiceException(
                    "Identity Service is unavailable: " + ex.getMessage(), ex);
        }
    }

    private <T> HttpEntity<T> buildHttpEntity(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
