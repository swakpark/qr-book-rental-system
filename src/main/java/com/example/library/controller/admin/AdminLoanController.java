package com.example.library.controller.admin;

import com.example.library.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class AdminLoanController {

    private final LoanService loanService;

    // 관리자 강제 반납
    @PostMapping("/{id}/force-return")
    public String forceReturn(@PathVariable Long id) {
        loanService.forceReturnBook(id);
        return "redirect:/admin/view/" + id;
    }
}