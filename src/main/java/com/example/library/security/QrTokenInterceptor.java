package com.example.library.security;

import com.example.library.qr.QrTokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class QrTokenInterceptor implements HandlerInterceptor {

    private final QrTokenValidator qrTokenValidator;

    public QrTokenInterceptor(QrTokenValidator qrTokenValidator) {
        this.qrTokenValidator = qrTokenValidator;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String path = request.getRequestURI(); // /qr/books/{id}

        // QR 접근 페이지가 아니면 통과
        if (!path.matches("/qr/books/\\d+")) {
            return true;
        }

        String token = request.getParameter("token");

        try {
            // 1. URL 경로에서 bookId 추출 (예: /qr/books/123 -> 123)
            String[] pathParts = path.split("/");
            Long bookId = Long.parseLong(pathParts[pathParts.length - 1]);

            // 2. validator의 validate 메서드 호출 (isValid가 아님)
            qrTokenValidator.validate(token, bookId);

            return true; // 검증 성공 시 통과
        } catch (Exception e) {
            // 검증 실패 시 403 에러 반환
            response.sendError(HttpStatus.FORBIDDEN.value(),
                    "Invalid QR Token: " + e.getMessage());

            return false; // 요청 차단
        }
    }
}
