package com.example.library.config;

import com.example.library.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 관리자 URL 보호
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // QR / fetch 기반
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            String redirect = request.getParameter("redirect");
                            if (redirect != null && !redirect.isBlank()) {
                                response.sendRedirect(redirect);
                            } else {
                                response.sendRedirect("/qr/entry");
                            }
                        })
                        .failureHandler((request, response, exception) -> {
                            String redirect = request.getParameter("redirect");

                            if (redirect != null && !redirect.isBlank()) {
                                response.sendRedirect("/login?error&redirect=" +
                                        java.net.URLEncoder.encode(redirect, "UTF-8"));
                            } else {
                                response.sendRedirect("/login?error");
                            }
                        })

                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/qr/entry")
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }
}
