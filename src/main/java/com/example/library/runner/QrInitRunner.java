package com.example.library.runner;

import com.example.library.service.qr.QrBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QrInitRunner implements CommandLineRunner{

    private final QrBatchService qrBatchService;

    @Override
    public void run(String... args) throws Exception {
        qrBatchService.generateBookQrs();
        qrBatchService.generateTableQrs();
        System.out.println("✅ QR 코드 생성 완료 (qr-output 폴더 확인)");
    }
}
