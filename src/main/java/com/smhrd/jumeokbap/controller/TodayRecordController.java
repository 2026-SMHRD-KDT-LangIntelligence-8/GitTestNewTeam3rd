package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.domain.Diary;
import com.smhrd.jumeokbap.domain.SpendingLog;
import com.smhrd.jumeokbap.dto.TodayRecordRequest;
import com.smhrd.jumeokbap.service.TodayRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/api")
@Controller
@RequiredArgsConstructor
public class TodayRecordController {

    private final TodayRecordService todayRecordService;

    @GetMapping("/saveLog")
    // 수동 입력 페이지
    public String showSaveLog(@RequestParam("userId") String userId, @RequestParam("date") String date, Model model) {
        model.addAttribute("userId", userId);
        model.addAttribute("selectedDate", date);
        return "saveLog";
    }

    @PostMapping("/record/updateMain/{logId}")
    @ResponseBody
    // 대표지출 건 저장
    public String updateMainRecord(@PathVariable("logId") Long logId) {
        try {
            todayRecordService.setAsMainRecord(logId);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    @PostMapping("/save")
    // 등록 저장
    public String saveRecord(@ModelAttribute TodayRecordRequest dto,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            todayRecordService.manualRecord(dto, imageFile);

            return "redirect:/api/recordMain/" + dto.getUserId() + "?date=" + dto.getRegDate();

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/main/" + dto.getUserId();
        }
    }

    @GetMapping("/delete/{logId}")
    // 삭제 기능
    public String deleteRecord(@PathVariable("logId") Long logId) {
        try {
            SpendingLog log = todayRecordService.getLogDetail(logId);
            String userId = log.getUserId();
            LocalDate regDate = log.getRegDate();
            todayRecordService.deleteRecord(logId);

            return "redirect:/api/recordMain/"+ userId + "?date=" + regDate;

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/recordMain/{userId}")
    // 메인화면
    public String getRecordMain(
            @PathVariable String userId,
            @RequestParam(value = "date", required = false) LocalDate date,
            Model model) {

        if (date == null) {
            date = LocalDate.now();
        }
        List<SpendingLog> logs = todayRecordService.getDailyTimeline(userId, date);

        model.addAttribute("list", logs);
        model.addAttribute("targetDate", date);
        model.addAttribute("userId", userId);

        return "recordMain";
    }

    @GetMapping("/recordDetail/{logId}")
    // 특정 지출 조회 기능
    public String getRecordDetail(@PathVariable("logId") Long logId, Model model){
        try {
            SpendingLog detail = todayRecordService.getLogDetail(logId);

            Diary diary = todayRecordService.getDiaryByLogId(logId);

            model.addAttribute("detail", detail);
            model.addAttribute("diary", diary);
            model.addAttribute("userId", detail.getUserId());

            return "recordDetail";
        } catch (Exception e) {
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

    @PostMapping("/record/toggleFixed/{diaryId}")
    @ResponseBody
    // 고정비 등록
    public String toggleFixed(@PathVariable Long diaryId) {
        try {
            todayRecordService.toggleFixedStatus(diaryId);
            return "success";
        } catch (Exception e) {
            return "fail";
        }
    }




}



