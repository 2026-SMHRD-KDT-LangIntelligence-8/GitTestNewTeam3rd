package com.smhrd.jumeokbap.controller;

import com.smhrd.jumeokbap.dto.UserLoginRequest;
import com.smhrd.jumeokbap.dto.UserSignupRequest;
import com.smhrd.jumeokbap.service.UserService;
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
    public ResponseEntity<String> login(@RequestBody UserLoginRequest dto) {
        String result = userService.login(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkUserId(@RequestParam String userId) {
        boolean exists = userService.isUserIdDuplicate(userId);
        return ResponseEntity.ok(exists);
    }
}
