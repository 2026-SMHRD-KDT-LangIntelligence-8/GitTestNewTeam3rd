package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.domain.SpendingLog;
import com.smhrd.jumeokbap.dto.TodayRecordRequest;
import com.smhrd.jumeokbap.service.TodayRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    // 전체기능조회
    public String getRecordMain(
            @PathVariable String userId,
            @RequestParam(value = "date", required = false) String date,
            Model model) {

        List<SpendingLog> logs;

        if (date == null || date.isEmpty()) {
            date = LocalDate.now().toString();
            logs = todayRecordService.getDailyTimeline(userId, date);
        }
        else {
            logs = todayRecordService.getDailyTimeline(userId, date);
        }
        model.addAttribute("list", logs);
        model.addAttribute("targetDate", date);

        return "recordMain";
    }

    @GetMapping("/recordDetail/{logId}")
    // 특정 지출 조회 기능
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
    // 소비메모 감성분석(flask연동)
    public String analyzeEmotion(@RequestParam("logId") Long logId,
                                 @RequestParam("content") String content){

        String emotion = todayRecordService.analyzeText(content);

        if(emotion.equals("1")) return "충동구매!!!💥";
        else return "잘 사셨어요!🍙";
    }




}



