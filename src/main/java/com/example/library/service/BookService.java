package com.example.library.service;

import com.example.library.dto.naver.NaverBookItem;
import com.example.library.model.Shelf;
import com.example.library.model.Zone;
import com.example.library.repository.AutoLocationProjection;
import com.example.library.repository.ShelfRepository;
import com.example.library.service.naver.NaverBookService;
import com.example.library.service.naver.NaverBookImportService;
import com.example.library.dto.CreateBookRequest;
import com.example.library.exception.BookNotFoundException;
import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final NaverBookService naverBookService;
    private final NaverBookImportService naverBookImportService;
    private final ZoneService zoneService;
    private final ShelfRepository shelfRepository;

    public BookService(BookRepository bookRepository,
                       NaverBookService naverBookService,
                       NaverBookImportService naverBookImportService,
                       ZoneService zoneService,
                       ShelfRepository shelfRepository) {
        this.bookRepository = bookRepository;
        this.naverBookService = naverBookService;
        this.naverBookImportService = naverBookImportService;
        this.zoneService = zoneService;
        this.shelfRepository = shelfRepository;
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
                .map(naverBookImportService::createBookFromNaver)
                .toList();
    }

    // 도서 등록
    public Book addBook(CreateBookRequest request) {

        // 1. 카테고리에 맞는 구역 할당
        Zone zone = zoneService.assignZone(request.getCategory());

        // 2. 도서 객체 생성 및 위치 정보(Shelf, Level) 설정
        Book book = new Book(
                request.getTitle(),
                request.getAuthor(),
                request.getIsbn(),
                request.getCategory(),
                request.getPublisher(),
                request.getImage(),
                zone
        );

        return registerBookAutoLocation(book);
    }

    @Transactional
    public Book registerBookAutoLocation(Book book) {

        Zone zone = book.getZone();

        // 1. 해당 Zone에서 비어있는 Shelf와 Level 찾기
        AutoLocationProjection location = shelfRepository
                .findFirstAvailableLocation(zone.getId())
                .orElseThrow(() ->
                        new IllegalStateException("해당 구역(" + zone.getName() + ")에 빈 자리가 없습니다.")
                );

        // 2. 찾은 ShelfId로 Shelf 엔티티 조회
        Shelf shelf = shelfRepository.findById(location.getShelfId())
                .orElseThrow(() ->
                        new BookNotFoundException("해당 서가를 찾을 수 없습니다.")
                );

        // 3. setter 대신 assignShelf 메서드 사용
        book.assignShelf(shelf, location.getShelfLevel());

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

    // 홈 화면 제목 검색 메서드
    public List<Book> searchByTitle(String keyword) {
        String normalizedKeyword = keyword.replace(" ", "");

        List<Book> allBooks = bookRepository.findAll();

        return allBooks.stream()
                .filter(book ->
                        book.getTitle().replace(" ", "")
                                .toLowerCase()
                                .contains(normalizedKeyword.toLowerCase())
                )
                .toList();
    }

    // Map 변환 메서드
    public Map<String, Long> getShelfBookCounts() {

        List<Object[]> result = bookRepository.countBooksByShelf();

        Map<String, Long> map = new HashMap<>();

        for (Object[] row : result) {
            String shelfCode = (String) row[0];
            Long count = (Long) row[1];
            map.put(shelfCode, count);
        }

        return map;
    }

    public List<Book> getBookByZone(Long zoneId) {
        return bookRepository.findByZoneId(zoneId);
    }
}
