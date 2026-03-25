package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.dto.UserLoginRequest;
import com.smhrd.jumeokbap.dto.UserSignupRequest;
import com.smhrd.jumeokbap.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public String signup(@RequestBody UserSignupRequest dto) {
        userService.signup(dto);
        return "회원가입 성공";
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest dto, HttpSession session) {
        String userId = userService.login(dto);

        // 세션에 로그인 사용자 아이디 저장
        session.setAttribute("loginUserId", userId);

        // 로그인 성공 메시지 반환
        return ResponseEntity.ok(userId + "님 로그인 성공");
    }

    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkUserId(@RequestParam String userId) {
        boolean exists = userService.isUserIdDuplicate(userId);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 성공");
    }

    @GetMapping("/session")
    public ResponseEntity<String> sessionCheck(HttpSession session) {
        String loginUserId = (String) session.getAttribute("loginUserId");

        if (loginUserId == null) {
            return ResponseEntity.status(401).body("로그인 안됨");
        }

        return ResponseEntity.ok("현재 로그인 사용자: " + loginUserId);
    }
}