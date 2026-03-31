package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.service.MainService;
import com.smhrd.jumeokbap.dto.MainDashboardResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MainViewController {

    @Autowired
    private org.springframework.context.ApplicationContext context; // 다른 컨트롤러 확인용 (생략가능)

    @Autowired
    private MainService mainService;

    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute("loginUserId") == null) {
            return "redirect:/login";
        }

        return "redirect:/main";
    }

    @GetMapping("/main")
    public String mainPage(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("loginUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        // 1. 대시보드 데이터 가져오기
        MainDashboardResponse data = mainService.getMainData(userId);

        // 2. 모델에 데이터 추가
        model.addAttribute("data", data);

        // 3. 뷰에서 쓸 userId 명시적 추가
        model.addAttribute("userId", userId);

        return "index";
    }

    // 캘린더 버튼을 눌렀을 때 실행되는 메서드
    @GetMapping("/dailyCalendar/{userId}")
    public String dailyCalendarPage(@PathVariable("userId") String userId, Model model) {
        model.addAttribute("userId", userId);

        return "dailyCalendar";
    }
}