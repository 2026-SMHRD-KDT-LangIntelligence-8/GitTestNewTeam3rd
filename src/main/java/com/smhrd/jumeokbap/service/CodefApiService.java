package com.smhrd.jumeokbap.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class CodefApiService {

    private final CodefTokenService codefTokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode post(String endpoint, String jsonBody) {
        HttpURLConnection conn = null;

        try {
            String accessToken = codefTokenService.getAccessToken();

            String baseUrl = "https://development.codef.io";

            // endpoint가 전체 URL이면 그대로 사용
            // endpoint가 /v1/... 같은 경로면 baseUrl 붙여서 사용
            String requestUrl;
            if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
                requestUrl = endpoint;
            } else {
                requestUrl = baseUrl + endpoint;
            }

            URL url = new URL(requestUrl);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            String responseBody = readAll(conn);
            String decodedBody = URLDecoder.decode(responseBody, StandardCharsets.UTF_8);

            System.out.println("CODEF 요청 URL = " + requestUrl);
            System.out.println("CODEF 응답 = " + decodedBody);

            return objectMapper.readTree(decodedBody);

        } catch (Exception e) {
            throw new RuntimeException("CODEF API 호출 실패: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private String readAll(HttpURLConnection conn) throws Exception {
        InputStream is = null;

        try {
            int status = conn.getResponseCode();
            is = (status >= 200 && status < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();
        } catch (Exception e) {
            is = conn.getErrorStream();
        }

        if (is == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }

        return sb.toString();
    }
}