package com.example.library.service;

public enum ChatState {
    NORMAL, // 기본 상태
    WAITING_FOR_EXTEND_SELECT, // 연장할 책 선택 대기
    WAITING_FOR_RETURN_SELECT // 반납할 책 선택 대기
}
