package com.fooddelivery.config;

import com.fooddelivery.model.User;
import com.fooddelivery.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            System.out.println("Seeding database with demo accounts...");

            // Seed Customer
            User customer = User.builder()
                    .name("John Doe")
                    .email("john@example.com")
                    .password(passwordEncoder.encode("password"))
                    .phone("1234567890")
                    .role(User.Role.CUSTOMER)
                    .active(true)
                    .build();
            userRepository.save(customer);

            // Seed Restaurant Owner
            User owner = User.builder()
                    .name("Raj Patel")
                    .email("raj@restaurant.com")
                    .password(passwordEncoder.encode("password"))
                    .phone("0987654321")
                    .role(User.Role.RESTAURANT_OWNER)
                    .active(true)
                    .build();
            userRepository.save(owner);
            
            // Seed Admin
            User admin = User.builder()
                    .name("System Admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin"))
                    .phone("1111111111")
                    .role(User.Role.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);

            System.out.println("Demo accounts seeded successfully.");
        }
    }
}
