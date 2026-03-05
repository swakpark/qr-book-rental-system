package com.example.library.qr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class QrTokenGenerator {

    @Value("${qr.secret}")
    private String secretKey;

    private static final String HMAC_ALGO = "HmacSHA256";

    public String generateSignature(Long bookId) {
        try {
            String data = String.valueOf(bookId);

            Mac mac = Mac.getInstance(HMAC_ALGO);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), HMAC_ALGO);

            mac.init(keySpec);

            byte[] hmacBytes = mac.doFinal(data.getBytes());

            // URL에 쓰기 좋게 Base64 URL Safe로 인코딩
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hmacBytes);

        } catch (Exception e) {
            throw new RuntimeException("QR HMAC 생성 실패", e);
        }
    }
}
