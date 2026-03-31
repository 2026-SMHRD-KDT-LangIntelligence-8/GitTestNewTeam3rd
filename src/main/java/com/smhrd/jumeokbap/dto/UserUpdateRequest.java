package com.smhrd.jumeokbap.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String nickname;
    private String email;
    private String phoneNumber;
}