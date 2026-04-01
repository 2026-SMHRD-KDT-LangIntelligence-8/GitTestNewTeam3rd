package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.dto.MainDashboardResponse;
import com.smhrd.jumeokbap.service.MainService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/main")
public class MainController {

    @Autowired
    private MainService mainService;

    @GetMapping("/dashboard")
    public ResponseEntity<MainDashboardResponse> getDashboardData(HttpSession session) {
        // 1. 세션에서 로그인한 유저 ID 꺼내오기
        String loginId = (String) session.getAttribute("loginUserId");

        // 2. 로그인 안 되어 있으면 기본 응답
        if (loginId == null) {
            return ResponseEntity.ok(MainDashboardResponse.builder()
                    .challengeName("로그인이 필요합니다")
                    .build());
        }

        // 3. 메인 데이터 조회
        MainDashboardResponse response = mainService.getMainData(loginId);

        return ResponseEntity.ok(response);
    }
}