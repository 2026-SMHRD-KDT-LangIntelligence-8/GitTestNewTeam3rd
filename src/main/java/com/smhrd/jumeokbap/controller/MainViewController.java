package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.service.MainService;
import com.smhrd.jumeokbap.dto.MainDashboardResponse; // 유저님이 만든 DTO
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainViewController {

    @Autowired
    private MainService mainService;

    // 메인 전 로그인 화면
    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute("loginUserId") == null) {
            return "redirect:/login"; // 로그인 안 했으면 로그인 창으로!
        }
        return "redirect:/main"; // 로그인 했으면 메인 대시보드로!
    }

    // 메인 화면
    @GetMapping("/main")
    public String mainPage(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("loginUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        // 1. 서비스에서 대시보드 데이터 긁어오기
        MainDashboardResponse data = mainService.getMainData(userId);

        // 2. 모델에 담아서 index.html로 보내기
        model.addAttribute("data", data);

        return "index";
    }
}