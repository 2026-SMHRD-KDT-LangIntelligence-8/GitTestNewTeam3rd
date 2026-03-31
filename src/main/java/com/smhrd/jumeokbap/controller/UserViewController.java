package com.smhrd.jumeokbap.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserViewController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    // 회원정보 페이지
    @GetMapping("/account_settings")
    public String accountSettingsPage(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("loginUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute("userId", userId);
        return "accountSettings";
    }

    // 회원 정보 수정 페이지
    @GetMapping("/edit-profile/{userId}")
    public String userEditPage(@PathVariable("userId") String userId, Model model){

        model.addAttribute("userId", userId);
        return "userEdit";
    }

    @GetMapping("/codef/connect-page")
    public String codefConnectPage(HttpSession session) {
        String userId = (String) session.getAttribute("loginUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        return "codef_connect";
    }
}