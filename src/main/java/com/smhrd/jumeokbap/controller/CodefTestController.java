package com.smhrd.jumeokbap.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smhrd.jumeokbap.service.CodefTokenService;

@RestController
public class CodefTestController {

    private final CodefTokenService codefTokenService;

    public CodefTestController(CodefTokenService codefTokenService) {
        this.codefTokenService = codefTokenService;
    }

    @GetMapping("/codef/token")
    public String getToken() {
        return codefTokenService.getAccessToken();
    }
}