package com.smhrd.jumeokbap.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@Entity
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(nullable = false)
    private String userId; // PK (사용자 입력)

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String birthDate;   // 생년월일

    @Column(nullable = false)
    private String nickname;    // 닉네임

    @Column(nullable = false)
    private String joinedAt;    // 가입일

    @Column(nullable = false)
    private String phoneNumber; // 전화번호

    @Column(nullable = false, unique = true)
    private String email;
}