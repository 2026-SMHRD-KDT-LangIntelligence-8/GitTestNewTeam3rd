package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.dto.CodefConnectedIdRequest;
import com.smhrd.jumeokbap.service.CodefConnectedIdService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/codef")
@RequiredArgsConstructor
public class CodefConnectController {

    private final CodefConnectedIdService codefConnectedIdService;

    @PostMapping("/connect")
    public ResponseEntity<?> connect(@RequestBody CodefConnectedIdRequest dto, HttpSession session) {

        String userId = (String) session.getAttribute("loginUserId");

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "message", "로그인이 필요합니다."
            ));
        }

        String connectedId = codefConnectedIdService.createConnectedId(userId, dto);

        return ResponseEntity.ok(Map.of(
                "message", "connectedId 발급 성공",
                "connectedId", connectedId
        ));
    }
}