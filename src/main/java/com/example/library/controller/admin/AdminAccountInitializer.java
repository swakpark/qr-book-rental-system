package com.example.library.controller.admin;

import com.example.library.model.Role;
import com.example.library.model.User;
import com.example.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {

        if (userRepository.existsByEmail(adminEmail)) {
            return; // 이미 있으면 아무것도 안 함
        }

        User admin = new User(
                "관리자",
                adminEmail,
                passwordEncoder.encode(adminPassword), // 암호화된 값 저장
                Role.ADMIN
        );

        userRepository.save(admin);

        System.out.println("✅ 관리자 계정 생성 완료: " + adminEmail);
    }
}
