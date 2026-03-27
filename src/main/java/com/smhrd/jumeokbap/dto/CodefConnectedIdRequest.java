package com.smhrd.jumeokbap.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodefConnectedIdRequest {

    // 카드사 기관코드
    private String organization;

    // 카드사 로그인 아이디
    private String loginId;

    // 카드사 로그인 비밀번호
    private String password;

    // 보통 개인은 P
    private String clientType = "P";

    // 카드는 CD
    private String businessType = "CD";

    // 일반적으로 1
    private String loginType = "1";

    // 선택: 화면 표시용 별칭
    private String accountAlias;
}