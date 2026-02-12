package com.example.library.controller.admin;

import com.example.library.service.LoanService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/view")
@RequiredArgsConstructor
public class AdminLoanViewController {

    private final LoanService loanService;

    @GetMapping("/loans")
    public String loanList(Model model) {
        model.addAttribute("loans", loanService.getAllLoans());
        return "admin/loan-list"; // templates/loan-list.html
    }
}
