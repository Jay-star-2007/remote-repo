package com.example.hello.security;

import com.example.hello.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUtils {

    /**
     * Get the details of the currently authenticated user.
     *
     * @return An Optional containing the UserDetails if a user is authenticated, or an empty Optional otherwise.
     */
    public static Optional<UserDetails> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }
        return Optional.ofNullable((UserDetails) authentication.getPrincipal());
    }

    /**
     * Get the username (phone number in our case) of the currently authenticated user.
     *
     * @return An Optional containing the username, or an empty Optional if no user is authenticated.
     */
    public static Optional<String> getCurrentUserUsername() {
        return getCurrentUser().map(UserDetails::getUsername);
    }
} 