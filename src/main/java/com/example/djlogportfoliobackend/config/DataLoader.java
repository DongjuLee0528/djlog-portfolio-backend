package com.example.djlogportfoliobackend.config;

import com.example.djlogportfoliobackend.entity.Admin;
import com.example.djlogportfoliobackend.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        createAdminIfNotExists();
    }

    private void createAdminIfNotExists() {
        if (adminRepository.findByUsername(adminUsername).isEmpty()) {
            String encodedPassword = passwordEncoder.encode(adminPassword);
            Admin admin = new Admin(adminUsername, encodedPassword);
            adminRepository.save(admin);
            log.info("[DATA_LOADER] Default admin user created: {}", adminUsername);
        } else {
            log.info("[DATA_LOADER] Admin user already exists: {}", adminUsername);
        }
    }
}