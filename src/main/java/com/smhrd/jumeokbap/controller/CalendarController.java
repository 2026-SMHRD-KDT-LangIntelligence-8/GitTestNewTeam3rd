package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/dailyCalendar/{userId}")
    public String showCalendar(@PathVariable("userId") String userId, Model model) {
        // 임시 소비 캘린더 확인용
        model.addAttribute("userId", userId);
        model.addAttribute("totalSpent", 985000);
        model.addAttribute("targetAmount", 1200000);
        model.addAttribute("impulseCount", 5);

        return "dailyCalendar";
    }

}
