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
    private final CodefCryptoService codefCryptoService;   // 추가

    public String connectAccount(String userId, CodefConnectRequest dto) {

        if (dto.getAccountType() != null &&
                !dto.getAccountType().isBlank() &&
                !"card".equalsIgnoreCase(dto.getAccountType()) &&
                !"CD".equalsIgnoreCase(dto.getAccountType())) {
            throw new IllegalArgumentException("카드 연결만 지원합니다.");
        }

        String businessType = "CD";
        String loginType = "1";


        // 여기서 평문 비밀번호를 RSA 암호화
        String encryptedPassword = codefCryptoService.encryptPassword(dto.getPassword());

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
                encryptedPassword
        );

        JsonNode response = codefApiService.callPostApi(
                "https://api.codef.io/v1/account/create",
                requestBody
        );

        System.out.println("CODEF 응답 = " + response.toPrettyString());
        System.out.println("CODEF 요청 바디 = " + requestBody);

        String connectedId = response.path("data").path("connectedId").asText();

        if (connectedId.isBlank()) {
            String resultCode = response.path("result").path("code").asText();
            String resultMessage = response.path("result").path("message").asText();
            throw new RuntimeException("connectedId 발급 실패 - code: " + resultCode + ", message: " + resultMessage);
        }

        String alias = (dto.getAccountAlias() == null || dto.getAccountAlias().isBlank())
                ? "내 카드"
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