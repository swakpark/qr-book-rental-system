package com.example.library.repository;

import com.example.library.model.ChatHistory;
import com.example.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    // 정렬된 히스토리 조회
    List<ChatHistory> findByUserOrderByCreatedAtAsc(User user);

    // 유저별 전체 삭제
    void deleteByUser(User user);
}
