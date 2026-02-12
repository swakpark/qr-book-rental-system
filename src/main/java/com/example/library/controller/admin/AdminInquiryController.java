package com.example.library.controller.admin;

import com.example.library.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/inquiries")
@RequiredArgsConstructor
public class AdminInquiryController {

    private final InquiryService inquiryService;

    // 미처리 문의
    @GetMapping
    public String pendingInquiries(Model model) {
        model.addAttribute("inquiries", inquiryService.getPendingInquiries());
        model.addAttribute("viewType", "pending");
        return "admin/inquiries";
    }

    // 전체 문의
    @GetMapping("/all")
    public String allInquiries(Model model) {
        model.addAttribute("inquiries", inquiryService.getAllInquiries());
        model.addAttribute("viewType", "all");
        return "admin/inquiries";
    }

    // 문의 처리 완료
    @PostMapping("/{id}/resolve")
    public String resolveInquiry(@PathVariable Long id) {
        inquiryService.resolveInquiry(id);
        return "redirect:/admin/inquiries";
    }

    // 관리자 답변
    @PostMapping("/{id}/reply")
    public String replyInquiry(@PathVariable Long id, @RequestParam String reply) {
        inquiryService.replyInquiry(id, reply);
        return "redirect:/admin/inquiries";
    }
}
