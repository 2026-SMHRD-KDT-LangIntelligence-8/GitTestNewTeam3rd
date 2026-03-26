package com.smhrd.jumeokbap.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
public class CodefApiService {

    private final CodefTokenService codefTokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CodefApiService(CodefTokenService codefTokenService) {
        this.codefTokenService = codefTokenService;
    }

    /**
     * CODEF API 공통 호출 메서드
     */
    public JsonNode callPostApi(String url, String requestBody) {

        String accessToken = codefTokenService.getAccessToken();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        System.out.println("CODEF raw response = " + response.getBody());

        try {
            String decoded = URLDecoder.decode(response.getBody(), StandardCharsets.UTF_8);
            System.out.println("디코딩 응답 = " + decoded);

            return objectMapper.readTree(decoded);
        } catch (Exception e) {
            throw new RuntimeException("CODEF 응답 파싱 실패", e);
        }
    }
}