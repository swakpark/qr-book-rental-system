package com.example.library.service;

import com.example.library.model.Inquiry;
import com.example.library.model.User;
import com.example.library.repository.InquiryRepository;
import com.example.library.service.policy.OverduePolicy;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
@Transactional
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final LoanService loanService;

    public InquiryService(InquiryRepository inquiryRepository, LoanService loanService) {
        this.inquiryRepository = inquiryRepository;
        this.loanService = loanService;
    }

    public void createInquiry(User user, String message) {
        OverduePolicy policy = loanService.evaluateOverduePolicy(user);

        Inquiry inquiry = new Inquiry(user, message, !policy.canBorrow(), policy.getOverdueDays(), policy.getPenaltyAmount());

        inquiryRepository.save(inquiry);
    }

    // 내가 보낸 전체 문의 개수
    public long countMyInquiries(User user) {
        return inquiryRepository
                .findByUserOrderByCreatedAtDesc(user)
                .size();
    }

    // 새 관리자 답변 개수 (아직 안 읽은 것)
    public long countUnreadReplies(User user) {
        return inquiryRepository
                .findByUserAndResolvedTrueAndUserCheckedFalse(user)
                .size();
    }

    // 사용자: 문의 조회
    public List<Inquiry> getUserInquiries(User user) {
        return inquiryRepository.findByUserOrderByCreatedAtDesc(user);
    }

    // 관리자: 미처리 문의
    public List<Inquiry> getPendingInquiries() {
        return inquiryRepository.findByResolvedFalseOrderByCreatedAtDesc();
    }

    // 관리자: 전체 문의
    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAllByOrderByCreatedAtDesc();
    }

    // 관리자: 문의 처리 완료
    public void resolveInquiry(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalStateException("문의가 존재하지 않습니다."));
        inquiry.markResolved();
    }

    // 관리자: 답변
    public void replyInquiry(Long inquiryId, String reply) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalStateException("문의가 존재하지 않습니다."));

        inquiry.reply(reply);
    }

    // 사용자: 관리자의 답변 확인
    public void markInquiriesAsChecked(User user) {
        List<Inquiry> unchecked = inquiryRepository.findByUserAndResolvedTrueAndUserCheckedFalse(user);

        for (Inquiry inquiry : unchecked) {
            inquiry.setUserChecked(true);
        }
    }
}