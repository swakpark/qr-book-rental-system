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

    // 핵심 키워드 셋 보강 (설명문에 흔히 나오는 단어 제외)
    private static final Map<String, List<String>> CATEGORY_KEYWORDS = Map.of(
            "900", List.of("역사", "세계사", "한국사", "지리", "고고학", "유적", "근현대사"),
            "800", List.of("소설", "장편소설", "시집", "희곡", "에세이", "문학선", "단편소설"),
            "700", List.of("언어학", "문법", "회화", "사전", "어휘", "외국어", "작문"),
            "600", List.of("미술", "음악", "조각", "회화", "전시", "공연", "예술가", "필름"),
            "500", List.of("공학", "의학", "농업", "요리", "가정학", "기술실무", "매뉴얼"),
            "400", List.of("자연과학", "물리학", "화학", "생물학", "천문", "수학적", "나노"),
            "300", List.of("정치학", "경제학", "법률", "행정", "교육학", "사회학", "통계"),
            "200", List.of("불교", "기독교", "성경", "신학", "교리", "힌두교", "신앙"),
            "100", List.of("철학", "심리학", "윤리학", "형이상학", "사상가", "논리학"),
            "000", List.of("컴퓨터공학", "데이터베이스", "도서관", "프로그래밍", "백과사전")
    );

    private String extractCode(NaverBookItem item) {

        // 제목과 설명 분리 (제목의 중요도가 훨씬 높음)
        String title = clean(item.getTitle()).toLowerCase();
        String description = clean(item.getDescription()).toLowerCase();

        String bestCode = "000";
        double bestScore = 0.0;

        // 점수가 같을 경우를 대비해서 특정 카테고리에 우선순위를 두고 싶으면
        // CATEGORY_KEYWORDS 정의 시 LinkedHashMap을 사용하여 순서를 고정하는 것
        for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {

            String code = entry.getKey();
            List<String> keywords = entry.getValue();

            // 1. 제목에서 키워드 매칭 (가중치 3.0)
            int titleScore = countMatches(title, keywords);

            // 2. 설명에서 키워드 매칭 (가중치 1.0)
            int descScore = countMatches(description, keywords);

            // 3. 최종 점수 계산
            double totalScore = (titleScore * 3.0) + descScore;

            // 4. 더 높은 점수가 나오면 갱신
            if (totalScore > bestScore) {
                bestScore = totalScore;
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
