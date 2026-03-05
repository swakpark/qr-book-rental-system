package com.example.library.qr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class QrTokenValidator {

    @Value("${qr.secret}")
    private String secretKey;

    private static final String HMAC_ALGO = "HmacSHA256";

    public void validate(Long bookId, String signature) {

        if (signature == null || signature.isBlank()) {
            throw new IllegalArgumentException("QR 서명이 없습니다.");
        }

        String expected = generateSignature(bookId);

        if (!expected.equals(signature)) {
            throw new IllegalArgumentException("QR 서명이 유효하지 않습니다.");
        }
    }

    private String generateSignature(Long bookId) {
        try {
            String data = String.valueOf(bookId);

            Mac mac = Mac.getInstance(HMAC_ALGO);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), HMAC_ALGO);

            mac.init(keySpec);

            byte[] hmacBytes = mac.doFinal(data.getBytes());

            return Base64.getUrlEncoder().withoutPadding().encodeToString(hmacBytes);

        } catch (Exception e) {
            throw new RuntimeException("QR HMAC 검증 실패", e);
        }
    }
}