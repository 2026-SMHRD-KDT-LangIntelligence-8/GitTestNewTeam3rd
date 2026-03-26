package com.smhrd.jumeokbap.service;

import com.smhrd.jumeokbap.domain.Diary;
import com.smhrd.jumeokbap.domain.SpendingLog;
import com.smhrd.jumeokbap.dto.TodayRecordRequest;
import com.smhrd.jumeokbap.repository.DiaryRepository;
import com.smhrd.jumeokbap.repository.SpendingLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
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
    public List<SpendingLog> getDailyTimeline(String userId) {
        return spendingLogRepository.findByUserIdOrderBySpentAtDesc(userId);
    }

    // 날짜별 조회
    public List<SpendingLog> getDailyTimeline(String userId, String date){
        return spendingLogRepository.findByUserIdAndRegDate(userId, date);
    }

    // 특정 내역 건의 상세페이지로 이동!
    public SpendingLog getLogDetail(Long logId) {
        return spendingLogRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("기록을 못찾겠어요! ID: " + logId));
    }

    // 감성분석(flask 주소) 연동
    public String analyzeText(String content) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://127.0.0.1:5000/predict";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("text", content);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            String result = restTemplate.postForObject(url, entity, String.class);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "분석 서버 연결 실패";
        }

    }
}

