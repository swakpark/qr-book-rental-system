package com.example.library.model;

import jakarta.persistence.*;

@Entity
@Table(name = "book", uniqueConstraints = {@UniqueConstraint(columnNames = "isbn")})
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    // 네이버 API 연동 필드
    @Column(nullable = false, unique = true)
    private String isbn;

    private String publisher;

    private String image;

    private boolean available = true;

    // JPA 사용하기 위한 기본 생성자
    protected Book() {}

    // 도서 생성자
    public Book(String title, String author, String isbn, String publisher, String image) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
        this.image = image;
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

    // Getter / Setter
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getImage() {
        return image;
    }

    // 대여 여부 (true = 대여 가능, false = 누가 빌려감)
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
