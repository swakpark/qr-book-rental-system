package com.example.library.controller.qr;

import com.example.library.model.User;
import com.example.library.security.AuthUtil;
import com.example.library.service.LoanService;
import com.example.library.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Comparator;

@Controller
@RequestMapping("/qr/loans")
public class QrLoanController {

    private final LoanService loanService;
    private final UserService userService;

    public QrLoanController(LoanService loanService, UserService userService) {
        this.loanService = loanService;
        this.userService = userService;
    }

    @GetMapping
    public String myLoans(Model model) {

        if (!AuthUtil.isLoggedIn()) {
            return "redirect:/login?redirect=/qr/loans";
        }

        User user = userService.getUserByEmail(AuthUtil.getEmail());
        LocalDate today = LocalDate.now();

        List<LoanView> loanViews =
                loanService.getActiveLoansByUser(user).stream()
                        .map(loan -> {
                            // NULL 체크로 500 에러 방지 (핵심)
                            LocalDate dueDate = (loan.getDueDate() != null) ? loan.getDueDate() : today;
                            long daysLeft = ChronoUnit.DAYS.between(today, dueDate);

                            // Book 정보 NULL 체크
                            Long bookId = (loan.getBook() != null) ? loan.getBook().getId() : 0L;
                            String title = (loan.getBook() != null) ? loan.getBook().getTitle() : "제목 없음";
                            String author = (loan.getBook() != null) ? loan.getBook().getAuthor() : "저자 없음";

                            return new LoanView(
                                    bookId,
                                    title,
                                    author,
                                    dueDate,
                                    daysLeft
                            );
                        })
                        .sorted(Comparator.comparingLong(LoanView::getDaysLeft))
                        .toList();

        model.addAttribute("loanViews", loanViews);
        return "qr/qr-loans";
    }

    /* "qr-loans 전용 ViewModel" */
    public static class LoanView {
        private final Long bookId;
        private final String title;
        private final String author;
        private final LocalDate dueDate;
        private final long daysLeft;

        public LoanView(Long bookId, String title, String author,
                        LocalDate dueDate, long daysLeft) {
            this.bookId = bookId;
            this.title = title;
            this.author = author;
            this.dueDate = dueDate;
            this.daysLeft = daysLeft;
        }

        public Long getBookId() { return bookId; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public LocalDate getDueDate() { return dueDate; }
        public long getDaysLeft() { return daysLeft; }
    }
}
