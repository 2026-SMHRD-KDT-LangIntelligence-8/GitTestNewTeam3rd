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
    @Column
    private String userId; // PK (사용자 입력)

    private String password;

    private String birthDate;   // 생년월일
    private String nickname;    // 닉네임
    private String joinedAt;    // 가입일
    private String phoneNumber; // 전화번호

    @Column(unique = true)
    private String email;

}
