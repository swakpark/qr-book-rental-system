package com.example.library.service.naver;

import com.example.library.dto.naver.NaverBookItem;
import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NaverBookImportService {

    private final BookRepository bookRepository;

    public Book importFromNaver(NaverBookItem item) {

        String isbn13 = extractIsbn13(item.getIsbn());
        if (isbn13 == null) {
            throw new IllegalArgumentException("ISBN 정보가 없는 도서입니다.");
        }

        return bookRepository.findByIsbn(isbn13)
                .orElseGet(() -> {
                    Book book = new Book(
                            clean(item.getTitle()),
                            clean(item.getAuthor()),
                            isbn13,
                            item.getPublisher() == null ? "" : item.getPublisher(),
                            item.getImage() == null ? "" : item.getImage()
                    );
                    return bookRepository.save(book);
                });
    }

    // <b> 같은 HTML 태그 제거
    private String clean(String text) {
        if (text == null) return "";
        return text.replaceAll("<[^>]*>", "").trim();
    }

    // 네이버 ISBN 포맷: "ISBN10 ISBN13"
    private String extractIsbn13(String rawIsbn) {
        if (rawIsbn == null || rawIsbn.isBlank()) return null;

        String[] parts = rawIsbn.split(" ");
        if (parts.length >= 2) {
            return parts[1];
        }
        if (parts.length == 1 && parts[0].length() == 13) {
            return parts[0];
        }
        return null;
    }
}
