package com.example.library.controller.qr;

import com.example.library.model.User;
import com.example.library.model.Loan;
import com.example.library.service.LoanService;
import com.example.library.service.UserService;
import com.example.library.service.InquiryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import com.example.library.security.AuthUtil;

@Controller
@RequestMapping("/qr")
public class QrEntryController {

    private final UserService userService;
    private final LoanService loanService;
    private final InquiryService inquiryService;

    public QrEntryController(UserService userService, LoanService loanService, InquiryService inquiryService) {
        this.userService = userService;
        this.loanService = loanService;
        this.inquiryService = inquiryService;
    }

    @GetMapping("/entry")
    public String entry(Model model) {

        if (AuthUtil.isLoggedIn()) {

            String email = AuthUtil.getEmail();
            User user = userService.getUserByEmail(email);

            model.addAttribute("user", user);
            model.addAttribute("isAdmin", AuthUtil.isAdmin());

            // 문의 요약
            model.addAttribute("inquiryCount", inquiryService.countMyInquiries(user));
            model.addAttribute("unreadInquiryCount", inquiryService.countUnreadReplies(user));


            // 대여 목록
            List<Loan> loans = loanService.getActiveLoansByUser(user);
            LocalDate today = LocalDate.now();

            List<LoanView> loanViews = loans.stream()
                    .map(loan -> {
                        LocalDate dueDate = loan.getDueDate() != null
                                ? loan.getDueDate()
                                : today;

                        long daysLeft = ChronoUnit.DAYS.between(today, dueDate);

                        return new LoanView(
                                loan.getBook().getId(),
                                loan.getBook().getTitle(),
                                dueDate,
                                daysLeft
                        );
                    })
                    .toList();

            model.addAttribute("loanViews", loanViews);
        }

        return "qr/qr-entry";
    }



    // 화면 전용 DTO (ViewModel)
    public static class LoanView {

        private final Long bookId;
        private final String title;
        private final LocalDate dueDate;
        private final long daysLeft;

        public LoanView(Long bookId, String title, LocalDate dueDate, long daysLeft) {
            this.bookId = bookId;
            this.title = title;
            this.dueDate = dueDate;
            this.daysLeft = daysLeft;
        }

        public Long getBookId() { return bookId; }
        public String getTitle() { return title; }
        public LocalDate getDueDate() { return dueDate; }
        public long getDaysLeft() { return daysLeft; }
    }
}