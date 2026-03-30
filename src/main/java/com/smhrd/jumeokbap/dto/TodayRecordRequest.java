package com.smhrd.jumeokbap.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodayRecordRequest {

    private String userId;
    private String date;

    private String amount;
    private String storeName;
    private String spentAt;
    private String imageUrl;
    private Boolean isManual;

    private String content;
    private String emotionTag;
    private Boolean isImpulsive;

    private String regDate;
    private Boolean isFixed;

}
