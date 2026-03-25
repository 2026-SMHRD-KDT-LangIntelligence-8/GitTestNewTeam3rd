package com.smhrd.jumeokbap.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "GOAL_SETTING")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget { // 예산(소비 목표 설정) 엔터티

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "GOAL_ID")
    private Long goalId; // 목표 ID

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHALLENGE_NAME")
    private String challengeName;

    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate; // 시작일

    @Column(name = "END_DATE", nullable = false)
    private LocalDate endDate; // 종료일

    @Column(name = "TOTAL_LIMIT", nullable = false)
    private Long totalLimit; // 총 예산 한도

    @Column(name = "FIXED_COST_SUM")
    private Long fixedCostSum; // 고정 지출 합계

    @Column(name = "IS_ACTIVE", columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean isActive; // 활성화 여부

    // 외래키 (FK) : USER 테이블의 USER_ID와 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_ID")
    private User user;

}
