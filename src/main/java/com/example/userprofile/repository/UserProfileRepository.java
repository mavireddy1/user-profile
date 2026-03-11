package com.example.userprofile.repository;

import com.example.userprofile.model.UserProfile;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository {

    UserProfile save(UserProfile userProfile);

    Optional<UserProfile> findById(String userId);

    Optional<UserProfile> findByEmail(String email);

    List<UserProfile> findAll();

    UserProfile update(UserProfile userProfile);

    void deleteById(String userId);

    boolean existsById(String userId);

    boolean existsByEmail(String email);
}
