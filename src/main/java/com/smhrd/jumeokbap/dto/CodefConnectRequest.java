package com.smhrd.jumeokbap.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodefConnectRequest {

    private String organization;
    private String loginId;
    private String password;
}