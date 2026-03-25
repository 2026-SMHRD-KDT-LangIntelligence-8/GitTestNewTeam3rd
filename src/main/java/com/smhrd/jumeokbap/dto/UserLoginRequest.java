package com.smhrd.jumeokbap.dto;


import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class UserLoginRequest {
    private String userId;
    private String password;
}
