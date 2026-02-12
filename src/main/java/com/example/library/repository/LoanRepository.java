package com.example.library.repository;

import com.example.library.model.Loan;
import com.example.library.model.Book;
import com.example.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    // 아직 반납되지 않은 대여 기록 찾기
    Optional<Loan> findByBookAndReturnedFalse(Book book);

    // 사용자 기준 미반납 도서 목록 (정책 계산용)
    List<Loan> findByUserAndReturnedFalse(User user);

    @Query("""
        select l
        from Loan l
        join fetch l.book
        where l.user = :user
        and l.returned = false
    """)
    // View 렌더링용 (화면 출력용)
    List<Loan> findActiveLoansWithBook(@Param("user") User user);
}
