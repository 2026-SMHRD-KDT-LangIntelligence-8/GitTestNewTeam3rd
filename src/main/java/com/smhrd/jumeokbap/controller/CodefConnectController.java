package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.dto.CodefConnectRequest;
import com.smhrd.jumeokbap.service.CodefConnectedIdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/codef")
@RequiredArgsConstructor
public class CodefConnectController {

    private final CodefConnectedIdService codefConnectedIdService;

    @PostMapping("/connect/card")
    public ResponseEntity<String> connectCard(@RequestBody CodefConnectRequest dto) {

        System.out.println("컨트롤러 진입 성공");

        String userId = "testUser";
        String connectedId = codefConnectedIdService.connectCard(userId, dto);

        return ResponseEntity.ok("connectedId 발급 성공: " + connectedId);
    }
}