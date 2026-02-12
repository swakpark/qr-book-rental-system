package com.example.library.repository;

import com.example.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    // ISBN로 도서 조회 (추후 관리자/챗봇/중복 처리용)
    Optional<Book> findByIsbn(String isbn);

    // ISBN 중복 체크 (네이버 API 등록 시 핵심)
    boolean existsByIsbn(String isbn);

    // DB에 저장된 책만 검색
    List<Book> findByTitleContainingOrAuthorContaining(String title, String author);

    // 제목 검색 (기존 기능 + 챗봇용)
    Optional<Book> findFirstByTitleContaining(String title);

    // 대여 가능한 도서 상위 N권
    List<Book> findTop10ByAvailableTrueOrderByIdAsc();
}
