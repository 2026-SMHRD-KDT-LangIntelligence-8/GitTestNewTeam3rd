package com.smhrd.jumeokbap.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodefConnectRequest {

    private String organization;   // 기관코드
    private String loginId;        // 아이디
    private String password;       // 비밀번호
    private String accountType;    // card or bank
    private String accountAlias;   // 별칭 (예: 내 국민카드, 생활비 통장)
    private String loginType;      // 기본값 1
}