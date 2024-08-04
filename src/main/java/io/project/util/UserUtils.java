package io.project.util;

import io.project.model.User;
import io.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUtils {

    public static final String ADMIN_EMAIL = "test@example.com";

    public static final String ADMIN_PASSWORD = "test";

    private final UserRepository userRepository;

    @Bean
    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        var email = authentication.getName();
        return userRepository.findByEmail(email).get();
    }

    @Bean
    public User getAdmin() {
        var admin = new User();
        admin.setEmail(ADMIN_EMAIL);
        admin.setPasswordDigest(UserUtils.ADMIN_PASSWORD);
        return admin;
    }
}
