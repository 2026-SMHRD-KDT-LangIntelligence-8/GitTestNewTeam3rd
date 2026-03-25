package com.smhrd.jumeokbap.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.smhrd.jumeokbap.domain.UserCodefAccount;
import com.smhrd.jumeokbap.dto.CodefConnectRequest;
import com.smhrd.jumeokbap.repository.UserCodefAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CodefConnectedIdService {

    private final CodefApiService codefApiService;
    private final UserCodefAccountRepository repository;

    public String connectCard(String userId, CodefConnectRequest dto) {

        String requestBody = """
        {
          "accountList": [
            {
              "countryCode": "KR",
              "businessType": "card",
              "clientType": "P",
              "organization": "%s",
              "loginType": "1",
              "id": "%s",
              "password": "%s"
            }
          ]
        }
        """.formatted(
                dto.getOrganization(),
                dto.getLoginId(),
                dto.getPassword()
        );

        JsonNode response = codefApiService.callPostApi(
                "https://api.codef.io/v1/account/create",
                requestBody
        );

        System.out.println("CODEF 응답 = " + response.toPrettyString());

        String connectedId = response.path("data").path("connectedId").asText();

        if (connectedId.isBlank()) {
            String resultCode = response.path("result").path("code").asText();
            String resultMessage = response.path("result").path("message").asText();
            throw new RuntimeException("connectedId 발급 실패 - code: " + resultCode + ", message: " + resultMessage);
        }

        UserCodefAccount account = UserCodefAccount.builder()
                .userId(userId)
                .connectedId(connectedId)
                .organization(dto.getOrganization())
                .accountAlias("카드")
                .build();

        repository.save(account);

        return connectedId;
    }
}