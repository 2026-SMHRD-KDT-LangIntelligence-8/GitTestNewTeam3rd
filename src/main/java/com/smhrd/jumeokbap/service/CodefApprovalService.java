package com.smhrd.jumeokbap.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smhrd.jumeokbap.domain.UserCodefAccount;
import com.smhrd.jumeokbap.dto.CodefApprovalRequest;
import com.smhrd.jumeokbap.repository.UserCodefAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CodefApprovalService {

    private final CodefApiService codefApiService;
    private final UserCodefAccountRepository userCodefAccountRepository;
    private final CodefCryptoService codefCryptoService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode getApprovalList(String userId, CodefApprovalRequest dto) {
        validate(dto);

        // 1. DB에서 connectedId 찾기
        UserCodefAccount account = userCodefAccountRepository
                .findByUserIdAndOrganizationAndBusinessType(userId, dto.getOrganization(), "CD")
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 카드 connectedId가 없습니다. 먼저 카드 연결을 진행해주세요."));

        // 2. 요청 바디 구성
        Map<String, Object> requestMap = new LinkedHashMap<>();
        requestMap.put("organization", dto.getOrganization());
        requestMap.put("startDate", dto.getStartDate());
        requestMap.put("endDate", dto.getEndDate());
        requestMap.put("orderBy", dto.getOrderBy() == null ? "0" : dto.getOrderBy());
        requestMap.put("inquiryType", dto.getInquiryType() == null ? "1" : dto.getInquiryType());
        requestMap.put("connectedId", account.getConnectedId());
        requestMap.put("memberStoreInfoType", dto.getMemberStoreInfoType() == null ? "0" : dto.getMemberStoreInfoType());

        // 선택값
        if (hasText(dto.getBirthDate())) {
            requestMap.put("birthDate", dto.getBirthDate());
        }

        if (hasText(dto.getCardName())) {
            requestMap.put("cardName", dto.getCardName());
        }

        if (hasText(dto.getDuplicateCardIdx())) {
            requestMap.put("duplicateCardIdx", dto.getDuplicateCardIdx());
        }

        if (hasText(dto.getCardNo())) {
            requestMap.put("cardNo", dto.getCardNo());
        }

        if (hasText(dto.getCardPassword())) {
            // 일부 카드사는 카드비밀번호 RSA 암호화 필요
            String encryptedCardPassword = codefCryptoService.encrypt(dto.getCardPassword());
            requestMap.put("cardPassword", encryptedCardPassword);
        }

        try {
            String jsonBody = objectMapper.writeValueAsString(requestMap);

            // 3. CODEF 승인내역 조회 API 호출
            return codefApiService.post("/v1/kr/card/p/account/approval-list", jsonBody);

        } catch (Exception e) {
            throw new RuntimeException("승인내역 조회 요청 생성 중 오류가 발생했습니다.", e);
        }
    }

    private void validate(CodefApprovalRequest dto) {
        if (dto == null) {
            throw new IllegalArgumentException("요청값이 없습니다.");
        }
        if (!hasText(dto.getOrganization())) {
            throw new IllegalArgumentException("organization은 필수입니다.");
        }
        if (!hasText(dto.getStartDate())) {
            throw new IllegalArgumentException("startDate는 필수입니다.");
        }
        if (!hasText(dto.getEndDate())) {
            throw new IllegalArgumentException("endDate는 필수입니다.");
        }

        if (hasText(dto.getStartDate()) && dto.getStartDate().length() != 8) {
            throw new IllegalArgumentException("startDate는 yyyyMMdd 형식이어야 합니다.");
        }
        if (hasText(dto.getEndDate()) && dto.getEndDate().length() != 8) {
            throw new IllegalArgumentException("endDate는 yyyyMMdd 형식이어야 합니다.");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}