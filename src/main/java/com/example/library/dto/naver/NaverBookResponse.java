package com.example.library.dto.naver;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class NaverBookResponse {

    private List<NaverBookItem> items;
}
