package com.taarifu_engine_api.config;

import com.taarifu_engine_api.modules.common.domain.enums.PasswordStrength;
import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserStatus;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserType;
import com.taarifu_engine_api.modules.userandrole.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RootUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Strong password: RootAdmin@2024!Secure
    private static final String ROOT_PASSWORD = "RootAdmin@2024!Secure";
    private static final String ROOT_EMAIL = "root@email.com";
    private static final String ROOT_USERNAME = "rootadmin";

    @Override
    public void run(String... args) throws Exception {
        initializeRootUser();
    }

    private void initializeRootUser() {
        try {
            // Check if any users exist
            long userCount = userRepository.count();
            
            if (userCount == 0) {
                log.info("No users found. Creating root admin user...");
                
                // Create root admin user
                User rootUser = createRootUser();
                userRepository.save(rootUser);
                
                log.info("Root admin user created successfully!");
                log.info("Username: {}", ROOT_USERNAME);
                log.info("Email: {}", ROOT_EMAIL);
                log.info("Password: {}", ROOT_PASSWORD);
                log.warn("IMPORTANT: Please change the root password after first login!");
                
            } else {
                log.info("Users already exist ({} users found). Skipping root user creation.", userCount);
            }
            
        } catch (Exception e) {
            log.error("Error initializing root user: ", e);
        }
    }

    private User createRootUser() {
        User user = new User();
        
        // Set basic properties
        user.setUsername(ROOT_USERNAME);
        user.setEmail(ROOT_EMAIL);
        user.setUserType(UserType.ADMIN);
        user.setStatus(UserStatus.ACTIVE);
        user.setPasswordStrength(PasswordStrength.STRONG);
        user.setRequirePasswordChange(false);
        
        // Hash the password
        String hashedPassword = passwordEncoder.encode(ROOT_PASSWORD);
        user.setPasswordHash(hashedPassword);
        
        // Ensure ULID is generated
        user.ensureUid();
        
        return user;
    }
}
