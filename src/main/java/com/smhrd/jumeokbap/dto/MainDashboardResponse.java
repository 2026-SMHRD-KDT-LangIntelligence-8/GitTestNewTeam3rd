package com.smhrd.jumeokbap.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MainDashboardResponse {

    private String challengeName; //챌린지 이름 (피그마 GOAL 자리)
    private long todayUsage; // 오늘 사용 금액
    private String bubbleMessage; // 먹밥이의 말풍선 메시지
    private double progressPercent; // 목표 대비 누적 사용량 (0 ~ 100 (%))
    private long accumulatedUsage; // 챌린지 시작부터 현재까지 누적 지출액
    private boolean isOverBudget; // 오늘 예산 초과 여부

}

