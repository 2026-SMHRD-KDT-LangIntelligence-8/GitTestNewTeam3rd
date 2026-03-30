package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.service.CalendarService;
import com.smhrd.jumeokbap.service.TodayRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class CalendarController {

    private final CalendarService calendarService;
    private final TodayRecordService todayRecordService;

    @GetMapping("/dailyCalendar/{userId}")
    public String showCalendar(@PathVariable("userId") String userId, Model model) {
    Map<String, Object> calendarData = calendarService.getCalendarData(userId);

        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Map<String, Long> fixedSummary = todayRecordService.getMonthlyTotal(userId, currentMonth);

        model.addAttribute("totalAmount", calendarData.getOrDefault("totalAmount", 0L));
        model.addAttribute("totalSpent", calendarData.getOrDefault("totalSpent",0L));
        model.addAttribute("impulseCount", calendarData.get("impulseCount"));
        model.addAttribute("calendarMap", calendarData.get("calendarMap"));
        model.addAttribute("daysInMonth", calendarData.get("daysInMonth"));
        model.addAttribute("dailyEmojis", calendarData.get("dailyEmojis"));

        model.addAttribute("totalWithFixed", fixedSummary.get("totalWithFixed"));
        model.addAttribute("totalWithoutFixed", fixedSummary.get("totalWithoutFixed"));

        return "dailyCalendar";
    }

}
