package com.smhrd.jumeokbap.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CodefApiService {

    private final CodefTokenService codefTokenService;

    public CodefApiService(CodefTokenService codefTokenService) {
        this.codefTokenService = codefTokenService;
    }

    /**
     * CODEF API 공통 호출 메서드
     * @param url 호출할 CODEF API 주소
     * @param requestBody JSON 문자열
     * @return CODEF 응답 문자열
     */
    public String callPostApi(String url, String requestBody) {
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

        return response.getBody();
    }
}