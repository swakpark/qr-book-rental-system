package com.example.library.controller.admin;

import com.example.library.model.Book;
import com.example.library.model.Loan;
import com.example.library.model.Zone;
import com.example.library.repository.ZoneRepository;
import com.example.library.service.BookService;
import com.example.library.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/view")
@RequiredArgsConstructor
public class AdminBookViewController {

    private final BookService bookService;
    private final LoanService loanService;
    private final ZoneRepository zoneRepository;

    @GetMapping("/{id}")
    public String bookPage(@PathVariable Long id, Model model) {

        Book book = bookService.getBook(id);
        Zone currentZone = book.getZone();

        List<Zone> zonesFloor1 = zoneRepository.findByFloor(1);
        List<Zone> zonesFloor2 = zoneRepository.findByFloor(2);

        int anchorX;
        int anchorY;
        Zone stairs;

        if (currentZone.getFloor() == 1) {
            anchorX = 24;
            anchorY = 18;
            stairs = zoneRepository.findByCodeAndFloor("STAIRS", 1).orElse(null);
        } else {
            anchorX = 24;
            anchorY = 18;
            stairs = zoneRepository.findByCodeAndFloor("STAIRS", 2).orElse(null);
        }

        model.addAttribute("book", book);
        model.addAttribute("currentZone", currentZone);
        model.addAttribute("zonesFloor1", zonesFloor1);
        model.addAttribute("zonesFloor2", zonesFloor2);
        model.addAttribute("stairs", stairs);
        model.addAttribute("anchorX", anchorX);
        model.addAttribute("anchorY", anchorY);
        model.addAttribute("shelfCounts", bookService.getShelfBookCounts());

        // 현재 대여 정보 조회
        Optional<Loan> activeLoan = loanService.getActiveLoan(book);
        model.addAttribute("loan", activeLoan.orElse(null));

        return "admin/book-information"; // templates/book-information.html
    }
}
