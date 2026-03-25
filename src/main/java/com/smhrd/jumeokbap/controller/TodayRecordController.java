package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.dto.TodayRecordRequest;
import com.smhrd.jumeokbap.service.TodayRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
@Controller
@RequiredArgsConstructor
public class TodayRecordController {

    private final TodayRecordService todayRecordService;

    @PostMapping("/save") // POST 방식으로 데이터를 보낼 거예요.

    public ResponseEntity<String> saveRecord(@RequestBody TodayRecordRequest dto) {

        try {

            todayRecordService.manualRecord(dto);

            return ResponseEntity.ok("기록 저장에 성공했습니다!");

        } catch (Exception e) {

            return ResponseEntity.status(500).body("저장 실패: " + e.getMessage());

        }

    }

}



