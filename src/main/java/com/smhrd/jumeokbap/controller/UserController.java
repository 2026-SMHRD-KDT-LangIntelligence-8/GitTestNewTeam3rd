package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.dto.UserLoginRequest;
import com.smhrd.jumeokbap.dto.UserSignupRequest;
import com.smhrd.jumeokbap.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserSignupRequest dto, HttpSession session) {
        userService.signup(dto);

        // 회원가입 직후 자동 로그인
        session.setAttribute("loginUserId", dto.getUserId());

        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest dto, HttpSession session) {
        String userId = userService.login(dto);

        // 로그인 성공 시 세션 저장
        session.setAttribute("loginUserId", userId);

        return ResponseEntity.ok(userId + "님 로그인 성공");
    }

    // 로그인 상태 확인
    @GetMapping("/session-check")
    public ResponseEntity<?> sessionCheck(HttpSession session) {
        String userId = (String) session.getAttribute("loginUserId");

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "loggedIn", false
            ));
        }

        return ResponseEntity.ok(Map.of(
                "loggedIn", true,
                "userId", userId
        ));
    }

    // 아이디 중복 체크
    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkUserId(@RequestParam String userId) {
        boolean exists = userService.isUserIdDuplicate(userId);
        return ResponseEntity.ok(exists);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 성공");
    }
}