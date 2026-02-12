package com.example.library.repository;

import com.example.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 로그인용 (이메일로 사용자 조회)
    Optional<User> findByEmail(String email);

    // 회원가입 시 이메일 중복 체크용
    boolean existsByEmail(String email);
}
