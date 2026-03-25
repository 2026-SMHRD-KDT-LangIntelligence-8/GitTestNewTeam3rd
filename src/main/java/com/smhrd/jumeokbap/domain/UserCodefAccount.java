package com.smhrd.jumeokbap.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_codef_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCodefAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 우리 서비스 사용자 아이디
    @Column(nullable = false)
    private String userId;

    // CODEF에서 발급받은 connectedId
    @Column(nullable = false, length = 300)
    private String connectedId;

    // 카드사 기관코드
    @Column(nullable = false, length = 50)
    private String organization;

    // 화면에 보여줄 별칭 (예: 신한카드, 국민카드)
    @Column(length = 100)
    private String accountAlias;

    // 생성일시
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 수정일시
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}