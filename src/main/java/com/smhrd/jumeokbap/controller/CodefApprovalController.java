package com.smhrd.jumeokbap.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.smhrd.jumeokbap.dto.CodefApprovalRequest;
import com.smhrd.jumeokbap.service.CodefApprovalService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/codef")
@RequiredArgsConstructor
public class CodefApprovalController {

    private final CodefApprovalService codefApprovalService;

    @PostMapping("/approval-list")
    public ResponseEntity<?> getApprovalList(@RequestBody CodefApprovalRequest dto,
                                             HttpSession session) {

        String loginUserId = (String) session.getAttribute("loginUserId");

        if (loginUserId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        JsonNode result = codefApprovalService.getApprovalList(loginUserId, dto);
        return ResponseEntity.ok(result);
    }
}