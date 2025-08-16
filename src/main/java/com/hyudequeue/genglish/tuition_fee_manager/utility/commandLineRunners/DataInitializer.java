package com.hyudequeue.genglish.tuition_fee_manager.utility.commandLineRunners;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.UserStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdminUser(UserRepository userRepository) {
        return args -> {
            boolean teacherExists = userRepository.existsByRole(RoleEnum.ADMIN);

            if (!teacherExists) {
                String adminEmail = "admin@example.com";
                User admin = User.builder()
                        .email(adminEmail)
                        .passwordHash(new BCryptPasswordEncoder().encode("Admin@123"))
                        .fullName("System Administrator")
                        .role(RoleEnum.ADMIN)
                        .status(UserStatusEnum.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                userRepository.save(admin);
                System.out.println("Admin user created.");
            } else {
                System.out.println("Teacher user already exists, skipping admin creation.");
            }
        };
    }

}