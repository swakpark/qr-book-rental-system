package com.example.library.qr;

import org.springframework.stereotype.Component;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Base64;

@Component
public class QrTokenValidator {

    private static final String SECRET_KEY = "qr-secret-key";

    public void validate(String token, Long bookId) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("QR 토큰이 없습니다.");
        }

        DecodedToken decoded = decode(token);

        validateBookId(decoded, bookId);
        validateExpiration(decoded);
        validateSignature(decoded);
    }

    private static DecodedToken decode(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":");

            Long decodedBookId = Long.parseLong(parts[0]);
            long issuedAt = Long.parseLong(parts[1]);
            long expiresAt = Long.parseLong(parts[2]);
            String signature = parts[3];

            return new DecodedToken(decodedBookId, issuedAt, expiresAt, signature);

        } catch (Exception e) {
            throw new IllegalArgumentException("QR 토큰 형식이 올바르지 않습니다.");
        }
    }

    // URL 조작 방지
    private void validateBookId(DecodedToken decoded, Long bookId) {
        if (!decoded.getBookId().equals(bookId)) {
            throw new IllegalArgumentException("QR 토큰과 도서 정보가 일치하지 않습니다.");
        }
    }

    // 캡쳐 QR / 재사용 공격 방지
    private void validateExpiration(DecodedToken decoded) {
        long now = System.currentTimeMillis();

        if (now > decoded.getExpiresAt()) {
            throw new IllegalArgumentException("QR 토큰이 만료되었습니다.");
        }
    }

    // QR URL 위조 방지
    private void validateSignature(DecodedToken decoded) {
        String expected = generateSignature(
                decoded.getBookId(),
                decoded.getIssuedAt(),
                decoded.getExpiresAt()
        );

        if (!expected.equals(decoded.getSignature())) {
            throw new IllegalArgumentException("QR 토큰 서명이 유효하지 않습니다.");
        }
    }

    private String generateSignature(long bookId, long issuedAt, long expiresAt) {
        String data = bookId + ":" + issuedAt + ":" + expiresAt + ":" + SECRET_KEY;
        return DigestUtils.sha256Hex(data);
    }
}
