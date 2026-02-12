package com.example.library.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.List;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/responses";
    private final RestTemplate restTemplate = new RestTemplate();

    public String ask(String userMessage, String libraryContext) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String systemPrompt = """
        너는 "도서관 안내 챗봇"이다.

        규칙:
        1) 반드시 아래 [도서관 컨텍스트]에 근거해서 답한다.
        2) 도서관(대여/반납/연장/이용/도서검색)과 무관한 질문이면 정중히 거절하고,
           사용자가 도서관 관련 질문을 하도록 예시 3개를 제시한다.
        3) 답변은 한국어로, 2~5문장으로 짧고 친절하게.
        4) 모르면 솔직히 모른다고 말하고 가능한 다음 행동을 안내한다.

        [도서관 컨텍스트]
        """ + libraryContext;

        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "input", List.of(
                        Map.of(
                                "role", "system",
                                "content", List.of(
                                        Map.of("type", "input_text", "text", systemPrompt)
                                )
                        ),
                        Map.of(
                                "role", "user",
                                "content", List.of(
                                        Map.of("type", "input_text", "text", userMessage)
                                )
                        )
                )
        );
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);
            Map<?, ?> responseBody = response.getBody();
            if (responseBody == null) return "🤖 응답을 받지 못했어요. 잠시 후 다시 시도해 주세요.";

            // responses API는 보통 output_text 필드를 제공
            Object outputText = responseBody.get("output_text");
            if (outputText != null) {
                return outputText.toString().trim();
            }

            // 혹시 output_text가 없을 때를 대비한 안전 파싱(옵션)
            Object output = responseBody.get("output");
            if (output instanceof List<?> outList && !outList.isEmpty()) {
                Object first = outList.get(0);
                if (first instanceof Map<?, ?> firstMap) {
                    Object content = firstMap.get("content");
                    if (content instanceof List<?> contentList && !contentList.isEmpty()) {
                        Object c0 = contentList.get(0);
                        if (c0 instanceof Map<?, ?> c0map) {
                            Object text = c0map.get("text");
                            if (text != null) return text.toString().trim();
                        }
                    }
                }
            }

            return "🤖 답변을 생성했지만 내용을 꺼내오지 못했어요. (파싱 이슈)";

        } catch (Exception e) {
            e.printStackTrace();
            return "🤖 지금은 답변을 준비하지 못했어요. 잠시 후 다시 시도해 주세요.";
        }
    }
}

