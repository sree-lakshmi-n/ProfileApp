package com.company.project.config;

import com.company.project.model.Role;
import com.company.project.model.User;
import com.company.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Check if users already exist
        if (userRepository.count() == 0) {
            // Create users with properly encoded passwords
            User admin = new User(
                "admin@example.com",
                passwordEncoder.encode("password123"),
                "Admin User",
                Role.ADMIN
            );
            
            User moderator = new User(
                "moderator@example.com",
                passwordEncoder.encode("password123"),
                "Moderator User",
                Role.MODERATOR
            );
            
            User standard = new User(
                "standard@example.com",
                passwordEncoder.encode("password123"),
                "Standard User",
                Role.STANDARD
            );
            
            // Save users
            userRepository.save(admin);
            userRepository.save(moderator);
            userRepository.save(standard);
            
            System.out.println("=== Users created successfully! ===");
        } else {
            System.out.println("=== Users already exist in database ===");
        }
        
        // Print all users
        System.out.println("=== Database Users ===");
        userRepository.findAll().forEach(user -> {
            System.out.println("Email: " + user.getEmail() + ", Role: " + user.getRole());
        });
    }
}