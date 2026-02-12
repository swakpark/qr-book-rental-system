package com.example.library.service;

import com.example.library.exception.*;
import com.example.library.model.Loan;
import com.example.library.model.User;
import com.example.library.model.Book;
import com.example.library.repository.LoanRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.policy.OverduePolicy;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // 도서 대여
    public Loan loanBook(User user, Long bookId) {

        // 1. 연체 정책 검사 (한 번만)
        OverduePolicy policy = evaluateOverduePolicy(user);
        if (!policy.canBorrow()) {
            throw new OverdueRestrictedException(
                    policy.getOverdueDays(),
                    policy.getPenaltyAmount()
            );
        }

        // 2. 도서 조회
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        // 3. 이미 대여 중인지 확인
        loanRepository.findByBookAndReturnedFalse(book)
                .ifPresent(l -> {
                    throw new BookAlreadyLoanedException(bookId);
                });

        // 4. 대여 처리
        Loan loan = new Loan(user, book);
        return loanRepository.save(loan);
    }

    // 도서 반납
    public void returnBook(User user, Long bookId) {
        Book book = bookRepository.findById(bookId)
                        .orElseThrow(() -> new BookNotFoundException(bookId));

        Loan loan = loanRepository.findByBookAndReturnedFalse(book)
                        .orElseThrow(() -> new LoanNotFoundException(bookId));

        // 소유권 검증
        if (!loan.getUser().getId().equals(user.getId())) {
            throw new SecurityException("본인이 대여한 도서만 반납할 수 있습니다.");
        }

        loan.returnBook();
        loanRepository.save(loan);
    }

    // 현재 대여 정보 (view)
    public Optional<Loan> getActiveLoan(Book book) {
        return loanRepository.findByBookAndReturnedFalse(book);
    }

    // 도서 전체 조회
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    // 사용자가 현재 대여 중인 도서 목록
    public List<Loan> getActiveLoansByUser(User user) {
        return loanRepository.findActiveLoansWithBook(user);
    }

    // 도서 대여 연장
    public void extendLoan(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        Loan loan = loanRepository.findByBookAndReturnedFalse(book)
                .orElseThrow(() -> new LoanNotFoundException(bookId));

        // 연장 로직은 Entity 담당
        loan.extend();

        loanRepository.save(loan);
    }

    public OverduePolicy evaluateOverduePolicy(User user) {

        LocalDate today = LocalDate.now();

        // 현재 대여 중인 책 중 하나라도 연체되면 정책 적용
        return loanRepository.findByUserAndReturnedFalse(user).stream()
                .map(loan -> {
                    LocalDate dueDate = loan.getDueDate();
                    if (dueDate == null) {
                        return new OverduePolicy(true, 0, 0);
                    }

                    long days = ChronoUnit.DAYS.between(dueDate, today);

                    if (days > 0) {
                        int overdueDays = (int) days;
                        int penalty = overdueDays * 500;
                        return new OverduePolicy(false, overdueDays, penalty);
                    }

                    return new OverduePolicy(true, 0, 0);
                })
                // 하나라도 연체면 바로 적용
                .filter(policy -> !policy.canBorrow())
                .findFirst()
                .orElse(new OverduePolicy(true, 0, 0));
    }

    // 관리자 도서 강제 반납
    public void forceReturnBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        Loan loan = loanRepository.findByBookAndReturnedFalse(book)
                .orElseThrow(() -> new LoanNotFoundException(bookId));

        loan.returnBook();
        loanRepository.save(loan);
    }
}