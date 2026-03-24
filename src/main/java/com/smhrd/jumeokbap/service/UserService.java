package com.smhrd.jumeokbap.service;


import com.smhrd.jumeokbap.domain.User;
import com.smhrd.jumeokbap.dto.UserSignupRequest;
import com.smhrd.jumeokbap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
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
}
