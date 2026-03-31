package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.domain.User;
import com.smhrd.jumeokbap.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserViewController {

    private final UserService userService;

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

        User user = userService.getUserById(userId);

        model.addAttribute("user", user);
        return "accountSettings";
    }

    // 회원 정보 수정 페이지
    @GetMapping("/edit-profile")
    public String userEditPage(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("loginUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        User user = userService.getUserById(userId);

        model.addAttribute("user", user);
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