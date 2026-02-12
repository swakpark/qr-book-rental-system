package com.example.library.service.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Component;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class QrGenerator {

    public void generate(String url, String filePath) throws Exception {
        BitMatrix matrix = new MultiFormatWriter()
                .encode(url, BarcodeFormat.QR_CODE, 300, 300);

        Path path = FileSystems.getDefault().getPath(filePath);

        // 폴더 없으면 생성
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        MatrixToImageWriter.writeToPath(matrix, "PNG", path);
    }
}
