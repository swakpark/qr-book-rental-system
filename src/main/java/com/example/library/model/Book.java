package com.example.library.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "book", uniqueConstraints = {@UniqueConstraint(columnNames = "isbn")})
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_id")
    private Shelf shelf;

    private Integer shelfLevel; // 1~4 단

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    // 네이버 API 연동 필드
    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String category;

    private String publisher;

    private String image;

    private boolean available = true;

    // JPA 사용하기 위한 기본 생성자
    protected Book() {}

    // 도서 생성자
    public Book(String title, String author, String isbn, String category, String publisher, String image, Zone zone) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.publisher = publisher;
        this.image = image;
        this.zone = zone;
        this.available = true;
    }

    // Loan 생성 시
    public void loan() {
        this.available = false;
    }

    // Loan 반납 시
    public void returnBook() {
        this.available = true;
    }

    // 일단 'A-1' 등 배치 메서드
    public void assignShelf(Shelf shelf, int level) {
        this.shelf = shelf;
        this.shelfLevel = level;
    }

    // 단 이름 변환 메서드
    public String getShelfLevelName() {
        if (shelfLevel == null) return "";

        return switch (shelfLevel) {
            case 1 -> "하단";
            case 2 -> "중하단";
            case 3 -> "중상단";
            case 4 -> "상단";
            default -> "";
        };
    }
}
