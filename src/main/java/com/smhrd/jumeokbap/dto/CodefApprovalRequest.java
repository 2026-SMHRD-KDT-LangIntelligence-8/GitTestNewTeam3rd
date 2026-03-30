package com.smhrd.jumeokbap.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodefApprovalRequest {

    // 필수
    private String organization;   // 카드사 기관코드
    private String startDate;      // yyyyMMdd
    private String endDate;        // yyyyMMdd

    // 선택
    private String orderBy = "0";              // 0: 최신순, 1: 과거순
    private String inquiryType = "1";          // 0: 카드별 조회, 1: 전체조회
    //private String cardName;                   // inquiryType=0일 때 사용
    //private String duplicateCardIdx;           // inquiryType=0이고 중복카드일 때 사용
    //private String cardNo;                     // 일부 카드사에서 필요
    //private String cardPassword;               // 일부 카드사에서 필요
    private String memberStoreInfoType = "1"; // 0: 미포함, 1: 가맹점 포함, 2: 부가세 포함, 3: 전체
    //private String birthDate;                 // 필요 시만 사용
}