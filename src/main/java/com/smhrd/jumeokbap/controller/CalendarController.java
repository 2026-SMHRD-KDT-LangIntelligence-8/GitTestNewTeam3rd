package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.dto.TodayRecordRequest;
import com.smhrd.jumeokbap.service.CalendarService;
import com.smhrd.jumeokbap.service.TodayRecordService;
import com.smhrd.jumeokbap.domain.Diary;
import com.smhrd.jumeokbap.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class CalendarController {

    private final CalendarService calendarService;
    private final TodayRecordService todayRecordService;
    private final DiaryRepository diaryRepository;

    /**
     * 1. 소비 캘린더 조회 (날짜 이동 포함)
     */
    @GetMapping("/dailyCalendar/{userId}")
    public String showCalendar(
            @PathVariable("userId") String userId,
            @RequestParam(value = "date", required = false) String date,
            Model model) {

        LocalDate targetDate = (date != null && !date.isEmpty())
                ? LocalDate.parse(date)
                : LocalDate.now();

        Map<String, Object> calendarData = calendarService.getCalendarData(userId, targetDate);

        String currentMonthStr = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Map<String, Long> fixedSummary = todayRecordService.getMonthlyTotal(userId, currentMonthStr);

        model.addAttribute("totalWithFixed", fixedSummary.getOrDefault("totalWithFixed", 0L));
        model.addAttribute("totalWithoutFixed", fixedSummary.getOrDefault("totalWithoutFixed", 0L));
        model.addAttribute("totalAmount", calendarData.getOrDefault("totalAmount", 0L));
        model.addAttribute("impulseCount", calendarData.get("impulseCount"));
        model.addAttribute("daysInMonth", calendarData.get("daysInMonth"));
        model.addAttribute("dailyEmojis", calendarData.get("dailyEmojis"));

        model.addAttribute("currentYear", targetDate.getYear());
        model.addAttribute("currentMonth", targetDate.getMonthValue());
        model.addAttribute("userId", userId);

        return "dailyCalendar";
    }

    /**
     * 2. 상세 페이지에서의 메모 및 감정 이모티콘 저장 로직
     * 🍙 [수정 포인트]: Diary 데이터가 없으면 새로 생성하여 저장하도록 로직 보완
     */
    @PostMapping("/record/updateDiary")
    @ResponseBody
    public String updateDiary(@RequestBody TodayRecordRequest request) {
        System.out.println("🍙 [저장 시도] 받은 logId: " + request.getLogId());

        try {
            // 1. logId 유효성 체크
            if (request.getLogId() == null || request.getLogId().isEmpty()) {
                System.out.println("❌ 에러: logId가 전달되지 않았습니다.");
                return "fail";
            }

            Long logId = Long.parseLong(request.getLogId());

            // 2. DB에서 찾기 (없으면 새로운 Diary 객체 생성)
            // orElseGet을 사용하여 '기록을 찾을 수 없습니다' 에러가 나지 않게 방어합니다.
            Diary diary = diaryRepository.findByLogId(logId)
                    .orElseGet(() -> {
                        System.out.println("ℹ️ ID " + logId + "에 대한 기존 일기가 없어 새로 생성합니다.");
                        Diary newDiary = new Diary();
                        newDiary.setLogId(logId);
                        newDiary.setUserId(request.getUserId()); // DTO에 userId가 있다면 세팅
                        newDiary.setRegDate(LocalDate.now());   // 기본 날짜 세팅
                        newDiary.setIsMain(true);                // 캘린더 표시를 위해 기본 true 설정
                        return newDiary;
                    });

            // 3. 내용 및 감정 업데이트
            diary.setContent(request.getContent());
            diary.setEmotionTag(request.getEmotionTag());

            // 4. 저장 (save는 데이터가 없으면 INSERT, 있으면 UPDATE를 수행함)
            diaryRepository.save(diary);

            System.out.println("✅ [저장 성공] logId: " + logId);
            return "success";

        } catch (Exception e) {
            System.out.println("❌ [저장 실패] 에러 발생:");
            e.printStackTrace();
            return "fail";
        }
    }
}