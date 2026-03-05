package com.example.library.dto;

import com.example.library.model.Role;
import com.example.library.model.User;
import lombok.Getter;

@Getter
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;

    public UserResponse(Long id, String name, String email, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
