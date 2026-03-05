package com.example.library.config;

import com.example.library.security.JwtAuthenticationFilter;
import com.example.library.security.JwtProvider;
import com.example.library.security.RefreshTokenService;
import com.example.library.service.CustomUserDetailsService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService userDetailsService;

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // JWT 인증 로직 처리를 위한 필터
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider, userDetailsService, refreshTokenService);
    }

    // 관리자 URL 보호
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // QR / fetch 기반

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

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
                .addFilterBefore(jwtAuthenticationFilter(),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            String username = authentication.getName();
                            String role = authentication.getAuthorities()
                                    .iterator()
                                    .next()
                                    .getAuthority();

                            String accessToken = jwtProvider.createToken(username, role);
                            String refreshToken = jwtProvider.createRefreshToken(username);

                            // Redis에 Refresh 저장
                            refreshTokenService.save(username, refreshToken, 604800000L);

                            // JWT를 쿠키에 저장
                            Cookie accessCookie = new Cookie("ACCESS_TOKEN", accessToken);
                            accessCookie.setHttpOnly(true);
                            accessCookie.setPath("/");
                            accessCookie.setMaxAge(60 * 30);

                            Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
                            refreshCookie.setHttpOnly(true);
                            refreshCookie.setPath("/");
                            refreshCookie.setMaxAge(60 * 60 * 24 * 7);

                            response.addCookie(accessCookie);
                            response.addCookie(refreshCookie);

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
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {

                            String username = authentication != null ? authentication.getName() : null;

                            if (username != null) {
                                refreshTokenService.delete(username);
                            }

                            Cookie accessCookie = new Cookie("ACCESS_TOKEN", null);
                            accessCookie.setMaxAge(0);
                            accessCookie.setPath("/");

                            Cookie refreshCookie = new Cookie("REFRESH_TOKEN", null);
                            refreshCookie.setMaxAge(0);
                            refreshCookie.setPath("/");

                            response.addCookie(accessCookie);
                            response.addCookie(refreshCookie);

                            response.sendRedirect("/qr/entry");
                        })
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }
}
