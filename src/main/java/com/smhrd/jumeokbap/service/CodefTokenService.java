package com.smhrd.jumeokbap.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.smhrd.jumeokbap.config.CodefProperties;
import com.smhrd.jumeokbap.dto.CodefTokenResponse;

@Service
public class CodefTokenService {

    private final CodefProperties codefProperties;

    // 토큰 캐싱
    private String cachedAccessToken;
    private LocalDateTime tokenExpiresAt;

    public CodefTokenService(CodefProperties codefProperties) {
        this.codefProperties = codefProperties;
    }

    /**
     * Access Token 반환 (있으면 재사용, 없으면 발급)
     */
    public String getAccessToken() {

        // 기존 토큰이 있고 아직 유효하면 그대로 사용
        if (cachedAccessToken != null && tokenExpiresAt != null
                && LocalDateTime.now().isBefore(tokenExpiresAt)) {
            return cachedAccessToken;
        }

        // 없으면 새로 발급
        return issueNewToken();
    }

    /**
     * CODEF 토큰 신규 발급
     */
    private String issueNewToken() {

        RestTemplate restTemplate = new RestTemplate();

        // 1. clientId:clientSecret
        String auth = codefProperties.getClientId() + ":" + codefProperties.getClientSecret();

        // 2. Base64 인코딩
        String encodedAuth = Base64.getEncoder()
                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        // 3. 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Basic " + encodedAuth);

        // 4. 바디 설정
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("scope", "read");

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        // 5. 요청
        ResponseEntity<CodefTokenResponse> response =
                restTemplate.postForEntity(
                        codefProperties.getTokenUrl(),
                        request,
                        CodefTokenResponse.class
                );

        CodefTokenResponse tokenResponse = response.getBody();

        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            throw new RuntimeException("CODEF 토큰 발급 실패");
        }

        // 6. 토큰 저장
        this.cachedAccessToken = tokenResponse.getAccessToken();

        long expiresIn = tokenResponse.getExpiresIn() != null
                ? tokenResponse.getExpiresIn()
                : 604800L; // 기본 1주일

        // 만료 1분 전에 재발급하도록 설정
        this.tokenExpiresAt = LocalDateTime.now().plusSeconds(expiresIn - 60);

        return this.cachedAccessToken;
    }
}