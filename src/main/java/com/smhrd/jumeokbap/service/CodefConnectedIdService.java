package com.smhrd.jumeokbap.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.smhrd.jumeokbap.domain.UserCodefAccount;
import com.smhrd.jumeokbap.dto.CodefConnectRequest;
import com.smhrd.jumeokbap.repository.UserCodefAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodefConnectedIdService {

    private final CodefApiService codefApiService;
    private final UserCodefAccountRepository repository;

    public String connectAccount(String userId, CodefConnectRequest dto) {

        String businessType = dto.getAccountType();
        String loginType = (dto.getLoginType() == null || dto.getLoginType().isBlank())
                ? "1" : dto.getLoginType();

        String requestBody = """
        {
          "accountList": [
            {
              "countryCode": "KR",
              "businessType": "%s",
              "clientType": "P",
              "organization": "%s",
              "loginType": "%s",
              "id": "%s",
              "password": "%s"
            }
          ]
        }
        """.formatted(
                businessType,
                dto.getOrganization(),
                loginType,
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

        String alias = (dto.getAccountAlias() == null || dto.getAccountAlias().isBlank())
                ? ("card".equalsIgnoreCase(businessType) ? "내 카드" : "내 계좌")
                : dto.getAccountAlias();

        UserCodefAccount account = UserCodefAccount.builder()
                .userId(userId)
                .connectedId(connectedId)
                .organization(dto.getOrganization())
                .accountAlias(alias)
                .build();

        repository.save(account);

        return connectedId;
    }

    public List<UserCodefAccount> getMyAccounts(String userId) {
        return repository.findByUserId(userId);
    }
}