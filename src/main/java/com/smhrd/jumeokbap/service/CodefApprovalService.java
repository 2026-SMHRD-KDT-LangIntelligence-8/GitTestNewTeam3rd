package com.smhrd.jumeokbap.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smhrd.jumeokbap.domain.SpendingLog;
import com.smhrd.jumeokbap.domain.UserCodefAccount;
import com.smhrd.jumeokbap.dto.CodefApprovalRequest;
import com.smhrd.jumeokbap.repository.SpendingLogRepository;
import com.smhrd.jumeokbap.repository.UserCodefAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CodefApprovalService {

    private final CodefApiService codefApiService;
    private final UserCodefAccountRepository userCodefAccountRepository;
    private final CodefCryptoService codefCryptoService;
    private final SpendingLogRepository spendingLogRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode getApprovalList(String userId, CodefApprovalRequest dto) {
        validate(dto);

        UserCodefAccount account = userCodefAccountRepository
                .findByUserIdAndOrganizationAndBusinessType(userId, dto.getOrganization(), "CD")
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 카드 connectedId가 없습니다. 먼저 카드 연결을 진행해주세요."));

        Map<String, Object> requestMap = new LinkedHashMap<>();
        requestMap.put("organization", dto.getOrganization());
        requestMap.put("startDate", dto.getStartDate());
        requestMap.put("endDate", dto.getEndDate());
        requestMap.put("orderBy", dto.getOrderBy() == null ? "0" : dto.getOrderBy());
        requestMap.put("inquiryType", dto.getInquiryType() == null ? "1" : dto.getInquiryType());
        requestMap.put("connectedId", account.getConnectedId());
        requestMap.put("memberStoreInfoType", dto.getMemberStoreInfoType() == null ? "0" : dto.getMemberStoreInfoType());

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
            String encryptedCardPassword = codefCryptoService.encrypt(dto.getCardPassword());
            requestMap.put("cardPassword", encryptedCardPassword);
        }

        try {
            String jsonBody = objectMapper.writeValueAsString(requestMap);

            JsonNode response = codefApiService.post("/v1/kr/card/p/account/approval-list", jsonBody);

            String resultCode = response.path("result").path("code").asText();
            String resultMessage = response.path("result").path("message").asText();
            String extraMessage = response.path("result").path("extraMessage").asText();

            System.out.println("승인내역 resultCode = " + resultCode);
            System.out.println("승인내역 resultMessage = " + resultMessage);
            System.out.println("승인내역 extraMessage = " + extraMessage);

            if (!"CF-00000".equals(resultCode)) {
                throw new RuntimeException("승인내역 조회 실패: " + resultMessage);
            }

            saveApprovalList(userId, response);

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("승인내역 조회 요청 생성 중 오류가 발생했습니다.", e);
        }
    }

    // 자동 동기화용 메서드 추가
    public JsonNode getApprovalListWithConnectedId(
            String connectedId,
            String organization,
            LocalDate startDate,
            LocalDate endDate
    ) {
        try {
            Map<String, Object> requestMap = new LinkedHashMap<>();
            requestMap.put("organization", organization);
            requestMap.put("startDate", startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            requestMap.put("endDate", endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            requestMap.put("orderBy", "0");
            requestMap.put("inquiryType", "1");
            requestMap.put("connectedId", connectedId);
            requestMap.put("memberStoreInfoType", "0");

            String jsonBody = objectMapper.writeValueAsString(requestMap);

            JsonNode response = codefApiService.post("/v1/kr/card/p/account/approval-list", jsonBody);

            String resultCode = response.path("result").path("code").asText();
            String resultMessage = response.path("result").path("message").asText();

            System.out.println("자동 승인내역 resultCode = " + resultCode);
            System.out.println("자동 승인내역 resultMessage = " + resultMessage);

            if (!"CF-00000".equals(resultCode)) {
                throw new RuntimeException("자동 승인내역 조회 실패: " + resultMessage);
            }

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("자동 승인내역 조회 요청 생성 중 오류가 발생했습니다.", e);
        }
    }

    private void saveApprovalList(String userId, JsonNode response) {
        JsonNode dataNode = response.path("data");

        if (dataNode.isMissingNode() || dataNode.isNull()) {
            System.out.println("dataNode 없음");
            return;
        }

        JsonNode approvalList;

        if (dataNode.isArray()) {
            approvalList = dataNode;
        } else {
            approvalList = dataNode.path("resApprovalList");
        }

        if (!approvalList.isArray() || approvalList.isEmpty()) {
            System.out.println("승인내역 리스트 없음");
            return;
        }

        System.out.println("승인내역 저장 시작");
        System.out.println("리스트 개수: " + approvalList.size());

        for (JsonNode item : approvalList) {
            try {
                String storeName = getText(item, "resMemberStoreName");
                String approvalAmountText = getText(item, "resUsedAmount");
                String approvalDateText = getText(item, "resUsedDate");
                String approvalTimeText = getText(item, "resUsedTime");

                int amount = parseAmount(approvalAmountText);
                LocalDate regDate = parseDate(approvalDateText);
                LocalDateTime spentAt = parseDateTime(approvalDateText, approvalTimeText);

                System.out.println("가맹점: " + storeName + ", 금액: " + amount + ", 날짜: " + regDate + ", 시간: " + spentAt);

                SpendingLog log = new SpendingLog();
                log.setUserId(userId);
                log.setStoreName(storeName);
                log.setAmount(amount);
                log.setRegDate(regDate);
                log.setSpentAt(spentAt);

                log.setIsManual(false);
                log.setIsMain(false);
                log.setIsImpulsive(false);
                log.setIsFixed(false);

                SpendingLog saved = spendingLogRepository.save(log);
                System.out.println("저장 성공 logId = " + saved.getLogId());

            } catch (Exception e) {
                System.out.println("한 건 저장 실패: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String getText(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);
        if (value == null || value.isNull()) {
            return "";
        }
        return value.asText().trim();
    }

    private int parseAmount(String amountText) {
        if (!hasText(amountText)) {
            return 0;
        }

        String numberOnly = amountText.replaceAll("[^0-9]", "");
        if (!hasText(numberOnly)) {
            return 0;
        }

        return Integer.parseInt(numberOnly);
    }

    private LocalDate parseDate(String dateText) {
        if (!hasText(dateText)) {
            return LocalDate.now();
        }

        return LocalDate.parse(dateText, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private LocalDateTime parseDateTime(String dateText, String timeText) {
        LocalDate date = parseDate(dateText);

        if (!hasText(timeText)) {
            return date.atStartOfDay();
        }

        String normalized = timeText.replaceAll("[^0-9]", "");

        if (normalized.length() == 6) {
            LocalTime time = LocalTime.parse(normalized, DateTimeFormatter.ofPattern("HHmmss"));
            return LocalDateTime.of(date, time);
        }

        if (normalized.length() == 4) {
            LocalTime time = LocalTime.parse(normalized, DateTimeFormatter.ofPattern("HHmm"));
            return LocalDateTime.of(date, time);
        }

        return date.atStartOfDay();
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