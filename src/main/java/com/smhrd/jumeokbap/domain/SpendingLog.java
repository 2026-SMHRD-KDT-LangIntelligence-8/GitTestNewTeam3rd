package com.smhrd.jumeokbap.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Builder
@Table(name = "spending_log")
public class SpendingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long logId;

    // 사용자 ID
    @Column(nullable = false)
    private String userId;

    // 계좌/카드 연결 ID (선택)
    private Long accountId;

    // 날짜
    private LocalDate regDate;

    // 금액
    private Integer amount;

    // 가맹점명
    private String storeName;

    // 사용 시각 (날짜 + 시간)
    private LocalDateTime spentAt;

    // 이미지 (영수증 등)
    private String imageUrl;

    // 수기 입력 여부
    private Boolean isManual;

    // 대표 소비 여부
    private Boolean isMain;

    // 충동 소비 여부
    private Boolean isImpulsive;

    // 고정 지출 여부
    private Boolean isFixed;

    // 감정 태그
    private String emotionTag;

    // 생성 시간
    private LocalDateTime createdAt;

    // 수정 시간
    private LocalDateTime updatedAt;

    // 생성 시 자동 세팅
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        // 기본값 세팅 (null 방지)
        if (this.isManual == null) this.isManual = false;
        if (this.isMain == null) this.isMain = false;
        if (this.isImpulsive == null) this.isImpulsive = false;
        if (this.isFixed == null) this.isFixed = false;
    }

    // 수정 시 자동 세팅
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}