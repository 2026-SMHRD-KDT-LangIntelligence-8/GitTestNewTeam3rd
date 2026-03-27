package com.smhrd.jumeokbap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainViewController {

    @GetMapping("/") // 주소창에 localhost:8087/ 만 쳤을 때
    public String mainPage() {
        // 타임리프가 알아서 templates/index.html을 찾아줌
        return "index";
    }
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // 👉 templates/login.html
    }
}
