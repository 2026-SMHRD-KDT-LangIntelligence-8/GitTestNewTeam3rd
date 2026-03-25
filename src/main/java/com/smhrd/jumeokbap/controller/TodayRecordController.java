package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.domain.SpendingLog;
import com.smhrd.jumeokbap.dto.TodayRecordRequest;
import com.smhrd.jumeokbap.service.TodayRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping
@Controller
@RequiredArgsConstructor
public class TodayRecordController {

    private final TodayRecordService todayRecordService;

    @PostMapping("/api/save")
    public ResponseEntity<String> saveRecord(@RequestBody TodayRecordRequest dto) {
        try {
            todayRecordService.manualRecord(dto);
            return ResponseEntity.ok("기록 저장 성공");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("저장 실패: " + e.getMessage());
        }
    }

    @GetMapping("/api/main/{userId}")
    public ResponseEntity<List<SpendingLog>> getDailyTimeline(@PathVariable String userId) {
        try {
            List<SpendingLog> logs = todayRecordService.getDailyTimeline(userId);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

}



