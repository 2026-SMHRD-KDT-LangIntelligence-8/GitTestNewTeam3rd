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
        // 1. 세션에서 로그인한 유저의 ID 꺼내오기
        // BudgetController에서 "loginUserId"로 저장했으니 똑같이 맞춰야함!
        String loginId = (String) session.getAttribute("loginUserId");

        // 2. 만약 로그인이 안 되어 있다면 (세션에 ID가 없다면)
        if (loginId == null) {
            // 빈 응답을 보내서 JS가 "챌린지가 없어요"를 띄우게 하거나 기본값을 보내기
            return ResponseEntity.ok(MainDashboardResponse.builder()
                    .challengeName("로그인이 필요합니다")
                    .build());
        }

        // 3. 로그인한 loginId로 데이터 조회
        MainDashboardResponse response = mainService.getMainData(loginId);

        return ResponseEntity.ok(response);
    }

}
