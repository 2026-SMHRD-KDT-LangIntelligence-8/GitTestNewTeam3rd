package com.smhrd.jumeokbap.dto;

import lombok.Getter;

@Getter
public class UserSignupRequest {

    private String userId;
    private String password;
    private String birthDate;
    private String nickname;
    private String phoneNumber;
    private String email;

}
