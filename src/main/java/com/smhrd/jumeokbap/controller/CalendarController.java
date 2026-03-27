package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/{userId}")
    public String showCalendar(@PathVariable("userId") String userId, Model model) {
    Map<String, Object> calendarData = calendarService.getCalendarData(userId);

        model.addAttribute("totalAmount", calendarData.getOrDefault("totalAmount", 0L));
        model.addAttribute("totalSpent", calendarData.getOrDefault("totalSpent",0L));
        model.addAttribute("impulseCount", calendarData.get("impulseCount"));
        model.addAttribute("calendarMap", calendarData.get("calendarMap"));
        model.addAttribute("daysInMonth", calendarData.get("daysInMonth"));

        return "daily_calendar";
    }

}
