package com.intellilearn.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.intellilearn.entity.User;
import com.intellilearn.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;


@Component
public class SecurityUtils {

    private final UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    
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