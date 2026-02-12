package com.example.library.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // 테이블명 명시
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 실명 또는 닉네임
    @Column(nullable = false)
    private String name;

    // 로그인 ID (이메일)
    @Column(nullable = false, unique = true)
    private String email;

    // 비밀번호 (암호화해서 저장)
    @Column(nullable = false)
    private String password;

    // 권한
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // JPA 사용하기 위한 기본 생성자
    protected User() {}

    // 일반 사용자 생성자
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = Role.USER;
    }

    // 관리자 생성자
    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getter
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    // 편의 메서드
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

}
