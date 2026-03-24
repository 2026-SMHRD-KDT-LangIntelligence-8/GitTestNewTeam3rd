package com.smhrd.jumeokbap.service;

import com.smhrd.jumeokbap.domain.Diary;
import com.smhrd.jumeokbap.domain.SpendingLog;
import com.smhrd.jumeokbap.dto.TodayRecordRequest;
import com.smhrd.jumeokbap.repository.DiaryRepository;
import com.smhrd.jumeokbap.repository.SpendingLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TodayRecordService {
    private final DiaryRepository diaryRepository;
    private final SpendingLogRepository spendingLogRepository;

    @Transactional
    public void saveTodayRecord(TodayRecordRequest dto) {
        SpendingLog spendingLog = SpendingLog.builder()
                .userId(dto.getUserId())
                .amount(Integer.valueOf(dto.getAmount()))
                .storeName(dto.getStoreName())
                .spentAt(dto.getSpentAt())
                .imageUrl(dto.getImageUrl())
                .isManual(String.valueOf(dto.getIsManual()))
                .build();

        SpendingLog saveLog = spendingLogRepository.save(spendingLog);
        Diary diary = Diary.builder()
                .userId(dto.getUserId())
                .content(dto.getContent())
                .emotionTag(dto.getEmotionTag())
                .sentimentScore(0.0)
                .isImpulsive(false)
                .logId(saveLog.getLogId())
                .build();


   }
}
