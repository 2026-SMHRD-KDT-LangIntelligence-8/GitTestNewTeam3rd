package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.dto.UserLoginRequest;
import com.smhrd.jumeokbap.dto.UserSignupRequest;
import com.smhrd.jumeokbap.dto.UserUpdateRequest;
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

        String nickname = userService.getNicknameByUserId(userId);

        return ResponseEntity.ok(Map.of(
                "loggedIn", true,
                "userId", userId,
                "nickname", nickname
        ));
    }

    // 아이디 중복 체크
    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkUserId(@RequestParam String userId) {
        boolean exists = userService.isUserIdDuplicate(userId);
        return ResponseEntity.ok(exists);
    }

    // 회원정보 수정
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody UserUpdateRequest dto, HttpSession session) {
        String userId = (String) session.getAttribute("loginUserId");

        if (userId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            userService.updateUser(userId, dto.getNickname(), dto.getEmail(), dto.getPhoneNumber());
            return ResponseEntity.ok("회원정보 수정 성공");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("회원정보 수정 실패");
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 탈퇴하기
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(HttpSession session) {
        String userId = (String) session.getAttribute("loginUserId");

        if (userId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            userService.deleteUser(userId);
            session.invalidate();
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("fail");
        }
    }
}