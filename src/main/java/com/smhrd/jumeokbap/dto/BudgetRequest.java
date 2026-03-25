package com.smhrd.jumeokbap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequest {
    private String challengeName; // 챌린지 이름
    private LocalDate startDate; // 시작일
    private LocalDate endDate; // 종료일
    private Long totalLimit; // 총 예산
    private Long fixedCostSum; // 고정 지출
    private String userId; // 로그인한 유저 Id
}
