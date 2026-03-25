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
    @Column(length = 400)
    private String content;
    @Column(nullable = false)
    private String emotionTag;
    private Double sentimentScore;
    private Boolean isImpulsive;
    @Column(nullable = false)
    private Long logId;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private Boolean isMain;


}
