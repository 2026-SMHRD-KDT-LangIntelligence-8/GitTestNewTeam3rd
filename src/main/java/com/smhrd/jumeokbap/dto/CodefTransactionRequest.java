package com.smhrd.jumeokbap.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodefTransactionRequest {

    private String organization;     // 은행 기관코드
    private String connectedId;      // CODEF connectedId
    private String account;          // 계좌번호
    private String startDate;        // 조회 시작일 (예: 20260301)
    private String endDate;          // 조회 종료일 (예: 20260326)
    private String orderBy;          // 정렬방식
    private String inquiryType;      // 조회구분
}