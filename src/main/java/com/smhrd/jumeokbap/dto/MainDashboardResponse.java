package com.smhrd.jumeokbap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MainDashboardResponse {

    private String challengeName;      // 챌린지 이름
    private long todayUsage;           // 오늘 사용 금액
    private String bubbleMessage;      // 먹밥이 말풍선 메시지
    private double progressPercent;    // 목표 대비 진행률 (0 ~ 100)
    private long accumulatedUsage;     // 챌린지 시작일부터 현재까지 누적 사용 금액
    private boolean overBudget;        // 오늘 예산 초과 여부
}