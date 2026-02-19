package com.example.library.qr;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class QrTokenGenerator {

    private static final String SECRET_KEY = "qr-secret-key";
    private static final long EXPIRE_DURATION_MS = 24 * 60 * 60 * 1000; // 시연을 위해 24시간으로 토큰 만료 시간 설정

    public String generate(Long bookId) {

        long issuedAt = System.currentTimeMillis();
        long expiresAt = issuedAt + EXPIRE_DURATION_MS;

        String signature = generateSignature(bookId, issuedAt, expiresAt);

        String rawToken = bookId + ":" + issuedAt + ":" + expiresAt + ":" + signature;

        return Base64.getEncoder().encodeToString(rawToken.getBytes());
    }

    private String generateSignature(long bookId, long issuedAt, long expiresAt) {
        String data = bookId + ":" + issuedAt + ":" + expiresAt + ":" + SECRET_KEY;
        return DigestUtils.sha256Hex(data);
    }
}
