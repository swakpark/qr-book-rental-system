package com.example.library.security;

import com.example.library.qr.QrTokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
public class QrTokenInterceptor implements HandlerInterceptor {

    private final QrTokenValidator qrTokenValidator;

    public QrTokenInterceptor(QrTokenValidator qrTokenValidator) {
        this.qrTokenValidator = qrTokenValidator;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String path = request.getRequestURI(); // /qr/books/{id}

        // QR 책 상세 경로만 검사
        if (!path.startsWith("^/qr/books/\\\\d+/[^/]+$")) {
            return true;
        }

        try {
            // PathVariable 가져오기
            Map<String, String> pathVariables =
                    (Map<String, String>) request.getAttribute(
                            HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE
                    );

            if (pathVariables == null) {
                return true;
            }

            Long bookId = Long.valueOf(pathVariables.get("bookId"));
            String signature = pathVariables.get("signature");

            qrTokenValidator.validate(bookId, signature);

            return true; // 검증 성공 시 통과
        } catch (Exception e) {
            // 검증 실패 시 403 에러 반환
            response.sendError(HttpStatus.FORBIDDEN.value(),
                    "Invalid QR Token: " + e.getMessage());

            return false; // 요청 차단
        }
    }
}
