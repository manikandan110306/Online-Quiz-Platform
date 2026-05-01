package com.examly.springapp;

import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class QuizManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizManagementSystemApplication.class, args);
    }

    /** Seed a default admin account on first startup and fix any NULL roles. */
    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository,
                                javax.sql.DataSource dataSource) {
        return args -> {
            // Step 1: Fix any existing rows with NULL role (migration safety)
            try (java.sql.Connection conn = dataSource.getConnection();
                 java.sql.Statement stmt = conn.createStatement()) {
                // Add column if missing, or just patch NULLs
                stmt.executeUpdate(
                    "UPDATE users SET role = 'STUDENT' WHERE role IS NULL OR role = ''"
                );
                System.out.println("✅ Patched NULL roles to STUDENT");
            } catch (Exception e) {
                System.out.println("⚠️ Could not patch roles (table may not exist yet): " + e.getMessage());
            }

            // Step 2: Upsert the admin account
            userRepository.findByEmail("admin@quizmaster.com").ifPresentOrElse(
                existing -> {
                    // Always ensure admin has ADMIN role (fixes case where it was seeded without role)
                    if (!"ADMIN".equals(existing.getRole())) {
                        existing.setRole("ADMIN");
                        userRepository.save(existing);
                        System.out.println("✅ Fixed admin role to ADMIN");
                    } else {
                        System.out.println("✅ Admin already exists with correct role");
                    }
                },
                () -> {
                    User admin = new User();
                    admin.setName("Super Admin");
                    admin.setEmail("admin@quizmaster.com");
                    admin.setPassword("admin123");
                    admin.setRole("ADMIN");
                    userRepository.save(admin);
                    System.out.println("✅ Default admin seeded: admin@quizmaster.com / admin123");
                }
            );
        };
    }
}