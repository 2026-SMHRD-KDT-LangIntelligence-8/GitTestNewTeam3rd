package com.smhrd.jumeokbap.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.smhrd.jumeokbap.dto.CodefApprovalRequest;
import com.smhrd.jumeokbap.dto.CodefConnectedIdRequest;
import com.smhrd.jumeokbap.service.CodefApprovalService;
import com.smhrd.jumeokbap.service.CodefConnectedIdService;
import com.smhrd.jumeokbap.service.UserCodefAccountService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/codef")
@RequiredArgsConstructor
public class CodefConnectController {

    private final CodefConnectedIdService codefConnectedIdService;
    private final CodefApprovalService codefApprovalService;
    private final UserCodefAccountService userCodefAccountService;

    // 카드 연결 + 승인내역 자동 저장
    @PostMapping("/connect")
    public ResponseEntity<?> connect(@RequestBody CodefConnectedIdRequest dto, HttpSession session) {

        String userId = (String) session.getAttribute("loginUserId");

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "message", "로그인이 필요합니다."
            ));
        }

        // 1. connectedId 발급 및 저장
        String connectedId = codefConnectedIdService.createConnectedId(userId, dto);

        // 2. 최근 30일 승인내역 자동 조회
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(30);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        CodefApprovalRequest approvalRequest = new CodefApprovalRequest();
        approvalRequest.setOrganization(dto.getOrganization());
        approvalRequest.setStartDate(startDate.format(formatter));
        approvalRequest.setEndDate(today.format(formatter));
        approvalRequest.setOrderBy("0");
        approvalRequest.setInquiryType("1");
        approvalRequest.setMemberStoreInfoType("0");

        JsonNode approvalResult = codefApprovalService.getApprovalList(userId, approvalRequest);

        int savedCount = 0;
        JsonNode dataNode = approvalResult.path("data");

        if (dataNode.isArray()) {
            savedCount = dataNode.size();
        } else if (dataNode.path("resApprovalList").isArray()) {
            savedCount = dataNode.path("resApprovalList").size();
        }

        return ResponseEntity.ok(Map.of(
                "message", "카드 연결 및 승인내역 저장 성공",
                "connectedId", connectedId,
                "savedCount", savedCount
        ));
    }

    // 필요하면 테스트용으로 남겨두는 승인내역 조회 API
    @PostMapping("/approval-list")
    public ResponseEntity<?> getApprovalList(@RequestBody CodefApprovalRequest dto, HttpSession session) {

        String userId = (String) session.getAttribute("loginUserId");

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "message", "로그인이 필요합니다."
            ));
        }

        JsonNode result = codefApprovalService.getApprovalList(userId, dto);
        return ResponseEntity.ok(result);
    }

    // 연결 카드 조회
    @GetMapping("/accounts")
    public ResponseEntity<?> getAccounts(HttpSession session) {
        String userId = (String) session.getAttribute("loginUserId");

        if (userId == null) {
            return ResponseEntity.status(401).body("연결된 카드가 없습니다.");
        }

        return ResponseEntity.ok(userCodefAccountService.getAccounts(userId));
    }

    // 연결 카드 삭제
    @DeleteMapping("/account/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        userCodefAccountService.deleteAccount(id);
        return ResponseEntity.ok("삭제 완료");
    }
}