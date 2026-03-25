package com.smhrd.jumeokbap.service;

import com.smhrd.jumeokbap.domain.Diary;
import com.smhrd.jumeokbap.domain.SpendingLog;
import com.smhrd.jumeokbap.dto.TodayRecordRequest;
import com.smhrd.jumeokbap.repository.DiaryRepository;
import com.smhrd.jumeokbap.repository.SpendingLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TodayRecordService {
    private final DiaryRepository diaryRepository;
    private final SpendingLogRepository spendingLogRepository;

    @Transactional
    // 수동입력
    public void manualRecord(TodayRecordRequest dto) {
        SpendingLog spendingLog = SpendingLog.builder()
                .userId(dto.getUserId())
                .amount(Integer.valueOf(dto.getAmount()))
                .storeName(dto.getStoreName())
                .spentAt(dto.getSpentAt())
                .imageUrl(dto.getImageUrl())
                .isManual("Y")
                .build();

        SpendingLog saveLog = spendingLogRepository.save(spendingLog);

        Diary diary = Diary.builder()
                .userId(dto.getUserId())
                .content(dto.getContent())
                .emotionTag(dto.getEmotionTag())
                .sentimentScore(0.0)
                .isImpulsive(false)
                .isMain(true)
                .logId(saveLog.getLogId())
                .build();

        diaryRepository.save(diary);

    }
    @Transactional(readOnly = true)
    // 조회기능
    public List<SpendingLog> getDailyTimeline(String userId){
        return spendingLogRepository.findByUserIdOrderBySpentAtDesc(userId);
    }


   }

