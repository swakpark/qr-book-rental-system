package com.example.library.service.naver;

import com.example.library.dto.naver.NaverBookItem;
import com.example.library.dto.naver.NaverBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NaverBookService {

    @Value("${naver.api.base-url}")
    private String baseUrl;

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<NaverBookItem> search(String keyword) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("query", keyword)
                .queryParam("display", 10)
                .build()
                .toUriString();

        ResponseEntity<NaverBookResponse> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        request,
                        NaverBookResponse.class
                );

        if (response.getBody() == null) {
            return List.of();
        }

        return response.getBody().getItems();
    }
}
