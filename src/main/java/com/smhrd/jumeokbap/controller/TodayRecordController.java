package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.domain.SpendingLog;
import com.smhrd.jumeokbap.dto.TodayRecordRequest;
import com.smhrd.jumeokbap.service.TodayRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@Controller
@RequiredArgsConstructor
public class TodayRecordController {

    private final TodayRecordService todayRecordService;

    @PostMapping("/save")
    // 수동 입력
    public ResponseEntity<String> saveRecord(@RequestBody TodayRecordRequest dto) {
        try {
            todayRecordService.manualRecord(dto);
            return ResponseEntity.ok("기록 저장 성공");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("저장 실패: " + e.getMessage());
        }
    }

    @GetMapping("/recordMain/{userId}")
    // 조회 기능
    public String getRecordMain(@PathVariable String userId, Model model) {
            List<SpendingLog> logs = todayRecordService.getDailyTimeline(userId);
            model.addAttribute("list",logs);
            return "recordMain";
    }

    @GetMapping("/recordDetail/{logId}")
    // 상세페이지
    public String getRecordDetail(@PathVariable("logId") Long logId, Model model){
        try{
            SpendingLog detail = todayRecordService.getLogDetail(logId);
            model.addAttribute("detail", detail);
            return "recordDetail";
        } catch (Exception e){
            e.printStackTrace();
            return "error";
        }
    }

    @PostMapping("/analyze")
    @ResponseBody
    public String analyzeEmotion(@RequestParam("logId") Long logId,
                                 @RequestParam("content") String content){

        String emotion = todayRecordService.analyzeText(content);

        if(emotion.equals("1")) return "충동구매!!!💥";
        else return "잘 사셨어요!🍙";
    }




}



