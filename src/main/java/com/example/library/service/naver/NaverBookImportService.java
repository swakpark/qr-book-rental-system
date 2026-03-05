package com.example.library.service.naver;

import com.example.library.dto.naver.NaverBookItem;
import com.example.library.model.Book;
import com.example.library.model.Zone;
import com.example.library.repository.BookRepository;
import com.example.library.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class NaverBookImportService {

    private final BookRepository bookRepository;
    private final ZoneService zoneService;

    public Book createBookFromNaver(NaverBookItem item) {

        String isbn13 = extractIsbn13(item.getIsbn());
        if (isbn13 == null) {
            throw new IllegalArgumentException("ISBN 정보가 없는 도서입니다.");
        }

        String code = extractCode(item);
        Zone zone = zoneService.assignZone(code);

        return new Book(
                clean(item.getTitle()),
                clean(item.getAuthor()),
                isbn13,
                code,
                item.getPublisher() == null ? "" : item.getPublisher(),
                item.getImage() == null ? "" : item.getImage(),
                zone
        );
    }

    // <b> 같은 HTML 태그 제거
    private String clean(String text) {
        if (text == null) return "";
        return text.replaceAll("<[^>]*>", "").trim();
    }

    // 네이버 ISBN 포맷: "ISBN10 ISBN13"
    private String extractIsbn13(String rawIsbn) {
        if (rawIsbn == null || rawIsbn.isBlank()) return null;

        String[] parts = rawIsbn.split(" ");
        if (parts.length >= 2) {
            return parts[1];
        }
        if (parts.length == 1 && parts[0].length() == 13) {
            return parts[0];
        }
        return null;
    }

    private static final Map<String, List<String>> CATEGORY_KEYWORDS = Map.of(
            "900", List.of("역사", "세계사", "한국사", "지리", "고고학", "여행", "전기"),
            "800", List.of("문학", "시", "소설", "수필", "희곡", "일기", "평론"),
            "700", List.of("언어", "한국어", "중국어", "일본어", "영어", "독일어", "프랑스어"),
            "600", List.of("예술", "조각", "건축", "회화", "사진", "음악", "공연예술", "체육"),
            "500", List.of("기술과학", "의학", "농업", "공학", "건축", "가정학"),
            "400", List.of("자연과학", "수학", "물리", "화학", "천문학", "지학", "생물학", "식물학", "동물학"),
            "300", List.of("사회과학", "정치", "경제", "사회학", "교육", "법률", "행정", "관습"),
            "200", List.of("종교", "비교종교학", "불교", "기독교", "도교", "힌두교"),
            "100", List.of("철학", "심리학", "윤리학", "동양철학", "서양철학"),
            "000", List.of("총류", "백과사전", "도서관학", "신문", "컴퓨터과학", "일반저작물")
    );

    private String extractCode(NaverBookItem item) {

        String text = (clean(item.getTitle()) + " " + clean(item.getDescription()))
                .toLowerCase();

        String bestCode = "000";
        int bestScore = 0;

        for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {

            String code = entry.getKey();
            List<String> keywords = entry.getValue();

            int score = countMatches(text, keywords);

            if (score > bestScore) {
                bestScore = score;
                bestCode = code;
            }
        }

        return bestCode;
    }

    private int countMatches(String text, List<String> keywords) {
        int score = 0;
        for (String keyword : keywords) {
            if (text.contains(keyword.toLowerCase())) {
                score++;
            }
        }
        return score;
    }
}
