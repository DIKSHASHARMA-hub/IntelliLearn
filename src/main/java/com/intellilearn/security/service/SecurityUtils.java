package com.intellilearn.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.intellilearn.entity.User;
import com.intellilearn.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * Resolves the currently authenticated user from the SecurityContext.
 * Use this instead of trusting a client-supplied id (e.g. studentId in a
 * request body or path variable) for "who am I" checks.
 */
@Component
public class SecurityUtils {

    private final UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Email (username) of the currently authenticated user, or null if none. */
    public String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }

        return principal.toString();
    }

    /** Loads the full User entity for the currently authenticated user. */
    public User getCurrentUser() {
        String email = getCurrentUserEmail();

        if (email == null) {
            throw new EntityNotFoundException("No authenticated user in context");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found: " + email));
    }

    public boolean isCurrentUser(Long userId) {
        return getCurrentUser().getId().equals(userId);
    }

    public boolean currentUserHasRole(String role) {
        return getCurrentUser().getRole().getName().equalsIgnoreCase(role);
    }
}