package com.smhrd.jumeokbap.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_codef_account")
@Getter
@Setter
@NoArgsConstructor
public class UserCodefAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "connected_id", nullable = false)
    private String connectedId;

    @Column(name = "organization", nullable = false)
    private String organization;

    @Column(name = "business_type", nullable = false)
    private String businessType;

    @Column(name = "client_type", nullable = false)
    private String clientType;

    @Column(name = "login_type", nullable = false)
    private String loginType;

    @Column(name = "login_id", nullable = false)
    private String loginId;

    @Column(name = "account_alias")
    private String accountAlias;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 🔥 추가
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 🔥 생성 시
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now; // ⭐ 이거 핵심
    }

    // 🔥 수정 시
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}