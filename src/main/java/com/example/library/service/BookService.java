package com.example.library.service;

import com.example.library.dto.naver.NaverBookItem;
import com.example.library.service.naver.NaverBookService;
import com.example.library.service.naver.NaverBookImportService;
import com.example.library.dto.CreateBookRequest;
import com.example.library.exception.BookNotFoundException;
import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final NaverBookService naverBookService;
    private final NaverBookImportService naverBookImportService;

    public BookService(BookRepository bookRepository, NaverBookService naverBookService, NaverBookImportService naverBookImportService) {
        this.bookRepository = bookRepository;
        this.naverBookService = naverBookService;
        this.naverBookImportService = naverBookImportService;
    }

    public List<Book> findAvailableTop(int limit) {
        // 지금은 10권 고정 (limit 확장 가능)
        return bookRepository.findTop10ByAvailableTrueOrderByIdAsc();
    }
    // DB 우선 검색
    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingOrAuthorContaining(
                keyword, keyword
        );
    }

    // 네이버 fallback 검색
    public List<Book> searchOrImport(String keyword) {

        List<Book> books = bookRepository
                .findByTitleContainingOrAuthorContaining(keyword, keyword);

        if (!books.isEmpty()) {
            return books;
        }

        // DB에 없으면 네이버 검색 → import
        List<NaverBookItem> items = naverBookService.search(keyword);

        return items.stream()
                .map(naverBookImportService::importFromNaver)
                .toList();
    }

    // 도서 등록
    public Book addBook(CreateBookRequest request) {
        Book book = new Book(
                request.getTitle(),
                request.getAuthor(),
                request.getIsbn(),
                request.getPublisher(),
                request.getImage()
        );
        return bookRepository.save(book);
    }

    //도서 단건 조회
    public Book getBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    // 도서 전체 조회
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // 도서 대여
    public void loanBook(Long id) {
        Book book = getBook(id);

        if(!book.isAvailable()) {
            throw new IllegalStateException("이미 대여 중인 도서입니다.");
        }
        book.loan(); // 엔티티 책임
    }

    // 도서 반납
    public void returnBook(Long id) {
        Book book = getBook(id);

        if (book.isAvailable()) {
            throw new IllegalStateException("이미 반납된 도서입니다.");
        }
        book.returnBook(); // 엔티티 책임
    }

    // 검색 메서드
    public Optional<Book> findByTitleContains(String title) {
        return bookRepository.findFirstByTitleContaining(title);
    }
}
