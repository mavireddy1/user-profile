package com.example.userprofile.repository;

import com.example.userprofile.model.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory HashMap-based repository for UserProfile.
 * The map is keyed by userId. This serves as the sample data store.
 */
@Repository
public class UserProfileRepositoryImpl implements UserProfileRepository {

    private final Map<String, UserProfile> userProfileStore = new HashMap<>();

    @Override
    public UserProfile save(UserProfile userProfile) {
        userProfileStore.put(userProfile.getUserId(), userProfile);
        return userProfile;
    }

    @Override
    public Optional<UserProfile> findById(String userId) {
        return Optional.ofNullable(userProfileStore.get(userId));
    }

    @Override
    public Optional<UserProfile> findByEmail(String email) {
        return userProfileStore.values().stream()
                .filter(profile -> email.equalsIgnoreCase(profile.getEmail()))
                .findFirst();
    }

    @Override
    public List<UserProfile> findAll() {
        return new ArrayList<>(userProfileStore.values());
    }

    @Override
    public UserProfile update(UserProfile userProfile) {
        userProfileStore.put(userProfile.getUserId(), userProfile);
        return userProfile;
    }

    @Override
    public void deleteById(String userId) {
        userProfileStore.remove(userId);
    }

    @Override
    public boolean existsById(String userId) {
        return userProfileStore.containsKey(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userProfileStore.values().stream()
                .anyMatch(profile -> email.equalsIgnoreCase(profile.getEmail()));
    }
}
