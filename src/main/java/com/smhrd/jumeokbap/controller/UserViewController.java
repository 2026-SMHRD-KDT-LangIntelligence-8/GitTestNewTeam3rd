package com.smhrd.jumeokbap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {

    // 회원가입 페이지 이동
    @GetMapping("/signup")
    public String signupPage() {
        return "signup"; // templates/signup.html
    }
}