package com.example.library.config;

import com.example.library.security.QrTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final QrTokenInterceptor qrTokenInterceptor;

    public WebConfig(QrTokenInterceptor qrTokenInterceptor) {
        this.qrTokenInterceptor = qrTokenInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(qrTokenInterceptor)
                .addPathPatterns("/qr/books/*") // QR Book만 가로챔
                .excludePathPatterns(
                        "/qr/books/*/loan/**",
                        "/qr/books/*/return/**"
                );
    }
}
