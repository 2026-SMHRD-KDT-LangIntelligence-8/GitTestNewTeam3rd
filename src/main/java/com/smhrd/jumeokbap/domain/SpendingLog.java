package com.smhrd.jumeokbap.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    private LocalDate regDate;
    private Integer amount;
    private String storeName;
    private String spentAt;
    private String imageUrl;
    private String isManual;

    @Column(nullable = false)
    private String userId;
    private Long accountId;
    @Column(length = 1)
    private String isFixed = "N";







}
