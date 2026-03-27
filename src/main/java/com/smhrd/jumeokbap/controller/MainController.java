package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.dto.MainDashboardResponse;
import com.smhrd.jumeokbap.service.MainService;
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
    public ResponseEntity<MainDashboardResponse> getDashboardData() {
        // 지금은 로그인 기능이 없으니 임시로 "testUser"를 보냅니다.
        // DB에 유저 ID가 "testUser"인 데이터가 있어야 에러가 안 나요!
        MainDashboardResponse response = mainService.getMainData("testUser");
        return ResponseEntity.ok(response);
    }
}

