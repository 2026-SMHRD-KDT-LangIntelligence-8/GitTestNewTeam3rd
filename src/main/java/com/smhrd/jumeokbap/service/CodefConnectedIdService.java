package com.smhrd.jumeokbap.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.smhrd.jumeokbap.config.CodefProperties;
import com.smhrd.jumeokbap.dto.CodefConnectedIdRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CodefConnectedIdService {

    private final CodefApiService codefApiService;
    private final CodefCryptoService codefCryptoService;
    private final UserCodefAccountService userCodefAccountService;

    public String createConnectedId(String userId, CodefConnectedIdRequest dto) {
        validate(dto);

        try {
            String businessType = dto.getBusinessType();
            String clientType = dto.getClientType();
            String loginType = dto.getLoginType();

            String encryptedPassword = codefCryptoService.encryptPassword(dto.getPassword());

            if (encryptedPassword == null || encryptedPassword.isBlank()) {
                throw new RuntimeException("비밀번호 암호화 실패");
            }

            System.out.println("businessType = " + businessType);
            System.out.println("clientType = " + clientType);
            System.out.println("organization = " + dto.getOrganization());
            System.out.println("loginType = " + loginType);
            System.out.println("loginId = " + dto.getLoginId());
            System.out.println("encryptedPassword length = " + encryptedPassword.length());
            System.out.println("PUBLIC_KEY prefix = " + CodefProperties.PUBLIC_KEY.substring(0, 20));

            String requestBody = """
            {
              "accountList": [
                {
                  "countryCode": "KR",
                  "businessType": "%s",
                  "clientType": "%s",
                  "organization": "%s",
                  "loginType": "%s",
                  "id": "%s",
                  "password": "%s"
                }
              ]
            }
            """.formatted(
                    businessType,
                    clientType,
                    dto.getOrganization(),
                    loginType,
                    dto.getLoginId(),
                    encryptedPassword
            );

            System.out.println("connectedId 요청 바디 = " + requestBody);

            JsonNode response = codefApiService.post(
                    "https://development.codef.io/v1/account/create",
                    requestBody
            );

            System.out.println("connectedId 응답 = " + response.toPrettyString());

            String connectedId = response.path("data").path("connectedId").asText();

            if (connectedId == null || connectedId.isBlank()) {
                String code = response.path("result").path("code").asText();
                String message = response.path("result").path("message").asText();
                String extraMessage = response.path("result").path("extraMessage").asText();

                throw new RuntimeException(
                        "connectedId 발급 실패"
                                + " | code=" + code
                                + " | message=" + message
                                + " | extraMessage=" + extraMessage
                );
            }

            userCodefAccountService.saveConnectedId(userId, connectedId, dto);

            return connectedId;

        } catch (Exception e) {
            throw new RuntimeException("connectedId 생성 중 오류: " + e.getMessage(), e);
        }
    }

    private void validate(CodefConnectedIdRequest dto) {
        if (dto == null) {
            throw new IllegalArgumentException("요청값이 없습니다.");
        }
        if (isBlank(dto.getOrganization())) {
            throw new IllegalArgumentException("organization은 필수입니다.");
        }
        if (isBlank(dto.getLoginId())) {
            throw new IllegalArgumentException("loginId는 필수입니다.");
        }
        if (isBlank(dto.getPassword())) {
            throw new IllegalArgumentException("password는 필수입니다.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}