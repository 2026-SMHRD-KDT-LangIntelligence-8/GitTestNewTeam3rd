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

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}