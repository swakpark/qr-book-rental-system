package com.example.library.service.qr;

import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import com.example.library.qr.QrTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QrBatchService {

    private final BookRepository bookRepository;
    private final QrGenerator qrGenerator;
    private final QrTokenGenerator qrTokenGenerator;

    public void generateBookQrs() throws Exception {
        for (Book book : bookRepository.findAll()) {

            // 1. QR 토큰 생성 (서버 내부)
            String token = qrTokenGenerator.generate(book.getId());

            // 2. 토큰이 포함된 QR URL 생성
            String url = "https://rylie-crunchier-paul.ngrok-free.dev/qr/books/"
                    + book.getId()
                    + "?token="
                    + token;

            // 3. QR 이미지 파일 생성
            String file = "qr-output/static-qr/books/book_" + book.getId() + ".png";
            qrGenerator.generate(url, file);
        }
    }

    public void generateTableQrs() throws Exception {
        for (int i = 1; i <= 10; i++) {
            String url = "https://rylie-crunchier-paul.ngrok-free.dev/qr/entry";
            String file = "qr-output/static-qr/tables/table_" + i + ".png";
            qrGenerator.generate(url, file);
        }
    }
}