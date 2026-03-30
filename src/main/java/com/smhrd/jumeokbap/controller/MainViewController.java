package com.smhrd.jumeokbap.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainViewController {

    @GetMapping("/")
    public String homePage(HttpSession session) {
        String loginUserId = (String) session.getAttribute("loginUserId");

        if (loginUserId == null) {
            return "redirect:/login";
        }

        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}