package com.smhrd.jumeokbap.domain;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Builder
public class SpendingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long logId;

    @Column(nullable = false)
    private Integer amount;
    @Column(nullable = false)
    private String storeName;
    @Column(nullable = false)
    private String spentAt;
    private String imageUrl;
    @Column(nullable = false)
    private String isManual;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private Long accountId;






}
