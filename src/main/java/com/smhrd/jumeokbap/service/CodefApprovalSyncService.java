package com.smhrd.jumeokbap.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.smhrd.jumeokbap.domain.SpendingLog;
import com.smhrd.jumeokbap.domain.UserCodefAccount;
import com.smhrd.jumeokbap.repository.SpendingLogRepository;
import com.smhrd.jumeokbap.repository.UserCodefAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CodefApprovalSyncService {

    private final UserCodefAccountRepository userCodefAccountRepository;
    private final SpendingLogRepository spendingLogRepository;
    private final CodefApprovalService codefApprovalService;

    public void syncLatestApprovals(String userId) {
        System.out.println("===== 로그인 후 승인내역 자동 동기화 시작 =====");
        System.out.println("동기화 대상 userId = " + userId);

        List<UserCodefAccount> accounts = userCodefAccountRepository.findByUserId(userId);
        System.out.println("계정 개수 = " + accounts.size());

        if (accounts.isEmpty()) {
            System.out.println("저장된 connectedId 없음");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(7);

        System.out.println("조회 기간 = " + startDate + " ~ " + today);

        for (UserCodefAccount account : accounts) {
            try {
                System.out.println("----------------------------------");
                System.out.println("account businessType = " + account.getBusinessType());

                if (!"CD".equalsIgnoreCase(account.getBusinessType())) {
                    System.out.println("카드 계정이 아니므로 건너뜀");
                    continue;
                }

                String connectedId = account.getConnectedId();
                String organization = account.getOrganization();

                System.out.println("조회 시작 connectedId = " + connectedId);
                System.out.println("조회 시작 organization = " + organization);

                if (connectedId == null || connectedId.isBlank()) {
                    System.out.println("connectedId 없음 → 건너뜀");
                    continue;
                }

                JsonNode result = codefApprovalService.getApprovalListWithConnectedId(
                        connectedId,
                        organization,
                        startDate,
                        today
                );

                System.out.println("CODEF result = " + result);

                saveApprovalList(userId, result);

            } catch (Exception e) {
                System.out.println("자동 승인내역 동기화 실패: " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("===== 로그인 후 승인내역 자동 동기화 종료 =====");
    }

    private void saveApprovalList(String userId, JsonNode root) {
        JsonNode dataNode = root.path("data");

        JsonNode list;

        if (dataNode.isArray()) {
            list = dataNode;
        } else {
            list = dataNode.path("resApprovalList");
        }

        if (!list.isArray() || list.isEmpty()) {
            System.out.println("자동 동기화 승인내역 없음");
            return;
        }

        System.out.println("자동 동기화 승인내역 건수 = " + list.size());

        for (JsonNode item : list) {
            try {
                String dateStr = item.path("resUsedDate").asText();
                String timeStr = item.path("resUsedTime").asText();
                String storeName = item.path("resMemberStoreName").asText("");
                int amount = parseAmount(item.path("resUsedAmount").asText());

                System.out.println(
                        "승인내역 확인 중 → date=" + dateStr
                                + ", time=" + timeStr
                                + ", store=" + storeName
                                + ", amount=" + amount
                );

                LocalDate regDate = LocalDate.parse(
                        dateStr,
                        DateTimeFormatter.ofPattern("yyyyMMdd")
                );

                LocalDateTime spentAt = parseDateTime(dateStr, timeStr);

                boolean exists = spendingLogRepository
                        .existsByUserIdAndRegDateAndSpentAtAndAmountAndStoreName(
                                userId,
                                regDate,
                                spentAt,
                                amount,
                                storeName
                        );

                if (exists) {
                    System.out.println("중복 승인내역 → 저장 생략");
                    continue;
                }

                SpendingLog log = new SpendingLog();
                log.setUserId(userId);
                log.setRegDate(regDate);
                log.setSpentAt(spentAt);
                log.setAmount(amount);
                log.setStoreName(storeName);

                log.setIsManual(false);
                log.setIsMain(false);
                log.setIsImpulsive(false);
                log.setIsFixed(false);

                spendingLogRepository.save(log);
                System.out.println("자동 승인내역 저장 완료");

            } catch (Exception e) {
                System.out.println("자동 승인내역 저장 실패: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private int parseAmount(String amountText) {
        if (amountText == null || amountText.trim().isEmpty()) {
            return 0;
        }

        String numberOnly = amountText.replaceAll("[^0-9]", "");
        if (numberOnly.isEmpty()) {
            return 0;
        }

        return Integer.parseInt(numberOnly);
    }

    private LocalDateTime parseDateTime(String dateText, String timeText) {
        LocalDate date = LocalDate.parse(dateText, DateTimeFormatter.ofPattern("yyyyMMdd"));

        String normalized = (timeText == null) ? "" : timeText.replaceAll("[^0-9]", "");

        if (normalized.length() == 6) {
            return LocalDateTime.parse(
                    dateText + normalized,
                    DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            );
        }

        if (normalized.length() == 4) {
            return LocalDateTime.parse(
                    dateText + normalized + "00",
                    DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            );
        }

        return date.atStartOfDay();
    }
}