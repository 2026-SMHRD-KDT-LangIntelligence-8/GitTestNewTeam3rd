package com.smhrd.jumeokbap.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {

    // 회원가입 페이지 이동
    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    // 로그인 페이지 이동
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // 메인 페이지 (세션 활용) - 지금은 테스트만 씀
    @GetMapping("/")
    public String homePage(HttpSession session, Model model) {

        String loginUserId = (String) session.getAttribute("loginUserId");

        model.addAttribute("loginUserId", loginUserId);

        return "index"; // templates/index.html
    }
}