package com.example.library.service.qr;

import com.example.library.qr.QrTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QrTokenService {

    private final QrTokenGenerator qrTokenGenerator;

    public String generateBookToken(Long bookId) {
        return qrTokenGenerator.generate(bookId);
    }
}
