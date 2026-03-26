package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.domain.UserCodefAccount;
import com.smhrd.jumeokbap.dto.CodefConnectRequest;
import com.smhrd.jumeokbap.service.CodefConnectedIdService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/codef")
@RequiredArgsConstructor
public class CodefConnectController {

    private final CodefConnectedIdService codefConnectedIdService;

    @PostMapping("/connect")
    public ResponseEntity<?> connect(@RequestBody CodefConnectRequest dto, HttpSession session) {

        System.out.println("컨트롤러 진입 성공");

        String userId = (String) session.getAttribute("loginUserId");

        if (userId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        String connectedId = codefConnectedIdService.connectAccount(userId, dto);

        return ResponseEntity.ok(Map.of(
                "message", "계정 연결 성공",
                "connectedId", connectedId
        ));
    }

    @GetMapping("/my-accounts")
    public ResponseEntity<?> myAccounts(HttpSession session) {

        String userId = (String) session.getAttribute("loginUserId");

        if (userId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        List<UserCodefAccount> accounts = codefConnectedIdService.getMyAccounts(userId);
        return ResponseEntity.ok(accounts);
    }
}