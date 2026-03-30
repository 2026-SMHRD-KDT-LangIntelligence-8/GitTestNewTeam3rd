package com.smhrd.jumeokbap.service;

import com.smhrd.jumeokbap.domain.Diary;
import com.smhrd.jumeokbap.domain.SpendingLog;
import com.smhrd.jumeokbap.dto.TodayRecordRequest;
import com.smhrd.jumeokbap.repository.DiaryRepository;
import com.smhrd.jumeokbap.repository.SpendingLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


@RequiredArgsConstructor
@Service
public class TodayRecordService {
    private final DiaryRepository diaryRepository;
    private final SpendingLogRepository spendingLogRepository;
    // 파이썬 서버와 통신하기 위한 도구
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    // 수동입력
    public void manualRecord(TodayRecordRequest dto, org.springframework.web.multipart.MultipartFile imageFile) {

        String fileName = "";
        if (imageFile != null && !imageFile.isEmpty()) {
            fileName = imageFile.getOriginalFilename();

        }
        LocalDate recordDate;

        if (dto.getRegDate() != null && !dto.getRegDate().isEmpty()) {
            // 💡 문자열 "2026-03-26" 등을 LocalDate 객체로 변환
            recordDate = LocalDate.parse(dto.getRegDate());
        } else {
            recordDate = LocalDate.now();
        }

        String resultFromServer = analyzeText(dto.getContent());
        boolean isImpulsive = "1".equals(resultFromServer);

        boolean existsDiary = diaryRepository.existsByUserIdAndRegDate(dto.getUserId(), recordDate);
        boolean isMain = !existsDiary;

        SpendingLog spendingLog = SpendingLog.builder()
                .userId(dto.getUserId())
                .amount(Integer.valueOf(dto.getAmount()))
                .storeName(dto.getStoreName())
                .spentAt(parseSpentAt(dto.getSpentAt()))
                .imageUrl(fileName)
                .isImpulsive(isImpulsive)
                .isMain(isMain)
                .regDate(recordDate)
                .isManual(true)
                .build();

        SpendingLog saveLog = spendingLogRepository.save(spendingLog);


        // 3. 콘솔 확인용 로그
        System.out.println("🤖 AI 분석 결과: " + resultFromServer);


        Diary diary = Diary.builder()
                .userId(dto.getUserId())
                .content(dto.getContent())
                .emotionTag(dto.getEmotionTag())
                .sentimentScore(0.0)
                .isImpulsive(isImpulsive)
                .isMain(isMain)
                .logId(saveLog.getLogId())
                .regDate(recordDate)
                .build();

        diaryRepository.save(diary);

    }

    // 고정비 등록
    public Map<String, Long> getMonthlyTotal(String userId, String yearMonthStr) {
        YearMonth ym = YearMonth.parse(yearMonthStr);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<Diary> diaries = diaryRepository.findMonthlyDiaries(userId, startDate, endDate);

        long totalWithFixed = 0;
        long totalWithoutFixed = 0;

        for (Diary d : diaries) {
            long amount = spendingLogRepository.findById(d.getLogId())
                    .map(SpendingLog::getAmount)
                    .orElse(0);

            totalWithFixed += amount;

            if (!Boolean.TRUE.equals(d.getIsFixed())) {
                totalWithoutFixed += amount;
            }
        }
        Map<String, Long> result = new HashMap<>();
        result.put("totalWithFixed", totalWithFixed);
        result.put("totalWithoutFixed", totalWithoutFixed);
        return result;
    }

    @Transactional(readOnly = true)
    // 조회기능
    public List<SpendingLog> getDailyTimeline(String userId) {
        return spendingLogRepository.findByUserIdOrderBySpentAtDesc(userId);
    }

    // 날짜별 조회
    public List<SpendingLog> getDailyTimeline(String userId, LocalDate date){
        List<SpendingLog> logs = spendingLogRepository.findByUserIdAndRegDate(userId, date);

        for(SpendingLog log : logs){
            diaryRepository.findByLogId(log.getLogId()).ifPresent(diary -> {
                log.setEmotionTag(diary.getEmotionTag());
                log.setIsImpulsive(diary.getIsImpulsive());
                log.setIsMain(diary.getIsMain());
            });
        }
        return logs;
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
            // Map으로 받아서 "label" 키의 값을 추출
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

            // 플라스크의 label이 숫자 1이면 "1", 아니면 "0"을 확실히 반환
            if (response != null && response.get("label") != null) {
                String label = String.valueOf(response.get("label")); // "1" 또는 "1.0" 등 대응
                return label.startsWith("1") ? "1" : "0";
            }
            return "0";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }

    }

    public Diary getDiaryByLogId(Long logId){
        return diaryRepository.findByLogId(logId)
                .orElse(new Diary());
    }

    @Transactional
    // 기록 삭제
    public void deleteRecord(Long logId) {
        diaryRepository.deleteByLogId(logId);
        spendingLogRepository.deleteById(logId);
    }

    @Transactional
    // 오늘의 대표 지출 건
    public void setAsMainRecord(Long logId) {
        Diary targetDiary = diaryRepository.findByLogId(logId)
                .orElseThrow(() -> new RuntimeException("해당 기록의 일기 정보를 찾을 수 없습니다."));

        List<Diary> dailyDiaries = diaryRepository.findByUserIdAndRegDate(targetDiary.getUserId(), targetDiary.getRegDate());
        List<SpendingLog> dailyLogs = spendingLogRepository.findByUserIdAndRegDate(targetDiary.getUserId(), targetDiary.getRegDate());

        for (Diary d : dailyDiaries) d.setIsMain(false);
        for (SpendingLog l : dailyLogs) l.setIsMain(false);

        targetDiary.setIsMain(true);
        spendingLogRepository.findById(logId).ifPresent(log -> log.setIsMain(true));
    }

    @Transactional
    // 고정비 등록
    public void toggleFixedStatus(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("해당 기록을 찾을 수 없습니다."));

        boolean currentStatus = (diary.getIsFixed() != null) ? diary.getIsFixed() : false;
        diary.setIsFixed(!currentStatus);

    }
    private LocalDateTime parseSpentAt(String spentAt) {
        try {
            if (spentAt == null || spentAt.isBlank()) {
                return LocalDateTime.now();
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(spentAt, formatter);

        } catch (Exception e) {
            return LocalDateTime.now();
        }

    }

}

