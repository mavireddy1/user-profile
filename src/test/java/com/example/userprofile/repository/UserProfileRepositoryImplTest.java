package com.example.userprofile.repository;

import com.example.userprofile.model.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfileRepositoryImplTest {

    private UserProfileRepositoryImpl repository;

    private UserProfile sampleProfile;

    @BeforeEach
    void setUp() {
        repository = new UserProfileRepositoryImpl();
        sampleProfile = UserProfile.builder()
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
    void save_shouldPersistAndReturnProfile() {
        UserProfile saved = repository.save(sampleProfile);
        assertThat(saved).isEqualTo(sampleProfile);
        assertThat(repository.existsById("user-1")).isTrue();
    }

    @Test
    void findById_shouldReturnProfileWhenExists() {
        repository.save(sampleProfile);
        Optional<UserProfile> found = repository.findById("user-1");
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo("user-1");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        Optional<UserProfile> found = repository.findById("non-existent");
        assertThat(found).isEmpty();
    }

    @Test
    void findByEmail_shouldReturnProfileWhenEmailMatches() {
        repository.save(sampleProfile);
        Optional<UserProfile> found = repository.findByEmail("john.doe@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void findByEmail_isCaseInsensitive() {
        repository.save(sampleProfile);
        Optional<UserProfile> found = repository.findByEmail("JOHN.DOE@EXAMPLE.COM");
        assertThat(found).isPresent();
    }

    @Test
    void findAll_shouldReturnAllProfiles() {
        UserProfile second = UserProfile.builder()
                .userId("user-2")
                .email("jane@example.com")
                .build();
        repository.save(sampleProfile);
        repository.save(second);

        List<UserProfile> all = repository.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void update_shouldReplaceExistingProfile() {
        repository.save(sampleProfile);
        UserProfile updated = UserProfile.builder()
                .userId("user-1")
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .build();
        repository.update(updated);

        Optional<UserProfile> found = repository.findById("user-1");
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Jane");
    }

    @Test
    void deleteById_shouldRemoveProfile() {
        repository.save(sampleProfile);
        repository.deleteById("user-1");
        assertThat(repository.existsById("user-1")).isFalse();
    }

    @Test
    void existsById_shouldReturnFalseWhenNotExists() {
        assertThat(repository.existsById("user-99")).isFalse();
    }

    @Test
    void existsByEmail_shouldReturnTrueWhenEmailExists() {
        repository.save(sampleProfile);
        assertThat(repository.existsByEmail("john.doe@example.com")).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnFalseWhenEmailNotExists() {
        assertThat(repository.existsByEmail("nobody@example.com")).isFalse();
    }
}
