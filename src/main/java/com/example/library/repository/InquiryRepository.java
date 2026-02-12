package com.example.library.repository;

import com.example.library.model.Inquiry;
import com.example.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    // 사용자 본인 문의 목록 조회
    List<Inquiry> findByUserOrderByCreatedAtDesc(User user);

    // 관리자: 미처리 문의 목록
    List<Inquiry> findByResolvedFalseOrderByCreatedAtDesc();

    // 관리자: 전체 문의 목록
    List<Inquiry> findAllByOrderByCreatedAtDesc();

    // 답변 완료됐지만 사용자가 아직 안 본 문의
    List<Inquiry> findByUserAndResolvedTrueAndUserCheckedFalse(User user);
}
