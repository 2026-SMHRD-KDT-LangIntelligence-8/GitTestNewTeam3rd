package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.dto.CodefConnectedIdRequest;
import com.smhrd.jumeokbap.service.CodefConnectedIdService;
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
    public ResponseEntity<?> connect(@RequestBody CodefConnectedIdRequest dto) {
        String connectedId = codefConnectedIdService.createConnectedId(dto);

        return ResponseEntity.ok(Map.of(
                "message", "connectedId 발급 성공",
                "connectedId", connectedId
        ));
    }
}