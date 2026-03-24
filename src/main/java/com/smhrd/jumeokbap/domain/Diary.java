package com.smhrd.jumeokbap.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long DiaryId;
    @Column(nullable = false, length = 400)
    private String content;
    @Column(nullable = false)
    private String emotionTag;
    @Column(nullable = false)
    private Double sentimentScore;
    @Column(nullable = false)
    private Boolean isImpulsive;
    @Column(nullable = false)
    private Long logId;
    @Column(nullable = false)
    private String userId;


}
