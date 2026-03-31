package com.smhrd.jumeokbap.service;

import com.smhrd.jumeokbap.domain.User;
import com.smhrd.jumeokbap.dto.UserLoginRequest;
import com.smhrd.jumeokbap.dto.UserSignupRequest;
import com.smhrd.jumeokbap.repository.BudgetRepository;
import com.smhrd.jumeokbap.repository.DiaryRepository;
import com.smhrd.jumeokbap.repository.SpendingLogRepository;
import com.smhrd.jumeokbap.repository.UserCodefAccountRepository;
import com.smhrd.jumeokbap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final SpendingLogRepository spendingLogRepository;
    private final DiaryRepository diaryRepository;
    private final UserCodefAccountRepository userCodefAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(UserSignupRequest dto) {

        // 아이디 중복 확인
        if (userRepository.existsById(dto.getUserId())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // User 객체 만들기
        User user = User.builder()
                .userId(dto.getUserId())
                .password(encodedPassword)
                .birthDate(dto.getBirthDate())
                .nickname(dto.getNickname())
                .joinedAt(java.time.LocalDateTime.now().toString())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .build();

        // 저장
        userRepository.save(user);
    }

    public String login(UserLoginRequest dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("아이디가 존재하지 않습니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 세션에 저장할 사용자 식별값 반환
        return user.getUserId();
    }

    public String getNicknameByUserId(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."))
                .getNickname();
    }

    // 회원 정보 전체 조회
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    // 회원정보 수정
    @Transactional
    public void updateUser(String userId,
                           String nickname,
                           String email,
                           String phoneNumber,
                           String password,
                           String passwordCheck) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 닉네임
        if (nickname != null && !nickname.trim().isEmpty()) {
            user.setNickname(nickname.trim());
        }

        // 이메일
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email.trim());
        }

        // 전화번호
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            user.setPhoneNumber(phoneNumber.trim());
        }

        // 비밀번호 (조건부 변경)
        if (password != null && !password.isEmpty()) {

            if (passwordCheck == null || !password.equals(passwordCheck)) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }

            user.setPassword(passwordEncoder.encode(password));
        }
    }

    // 아이디 중복 확인
    public boolean isUserIdDuplicate(String userId) {
        return userRepository.existsById(userId);
    }

    // 탈퇴하기
    @Transactional
    public void deleteUser(String userId) {

        // 1. 자식 데이터 먼저 삭제
        budgetRepository.deleteByUser_UserId(userId);
        spendingLogRepository.deleteByUserId(userId);
        diaryRepository.deleteByUserId(userId);
        userCodefAccountRepository.deleteByUserId(userId);

        // 2. 마지막에 부모 삭제
        userRepository.deleteById(userId);
    }
}