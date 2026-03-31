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
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class TodayRecordService {
    private final DiaryRepository diaryRepository;
    private final SpendingLogRepository spendingLogRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    // 수동입력 및 초기 저장
    public void manualRecord(TodayRecordRequest dto, org.springframework.web.multipart.MultipartFile imageFile) {

        String fileName = "";
        if (imageFile != null && !imageFile.isEmpty()) {
            fileName = imageFile.getOriginalFilename();
        }

        LocalDate recordDate;
        if (dto.getRegDate() != null && !dto.getRegDate().isEmpty()) {
            recordDate = LocalDate.parse(dto.getRegDate());
        } else {
            recordDate = LocalDate.now();
        }

        String resultFromServer = analyzeText(dto.getContent());
        boolean isImpulsive = "1".equals(resultFromServer);
        boolean existsDiary = diaryRepository.existsByUserIdAndRegDate(dto.getUserId(), recordDate);
        boolean isMain = !existsDiary;

        Integer amountValue = 0;
        try {
            if (dto.getAmount() != null && !dto.getAmount().trim().isEmpty()) {
                amountValue = Integer.valueOf(dto.getAmount().replace(",", ""));
            }
        } catch (NumberFormatException e) {
            amountValue = 0;
        }

        SpendingLog spendingLog = SpendingLog.builder()
                .userId(dto.getUserId())
                .amount(amountValue)
                .storeName(dto.getStoreName())
                .spentAt(dto.getSpentAt() != null ? parseSpentAt(dto.getSpentAt()) : LocalDateTime.now())
                .imageUrl(fileName)
                .isImpulsive(isImpulsive)
                .isMain(isMain)
                .regDate(recordDate)
                .isManual(true)
                .build();

        SpendingLog saveLog = spendingLogRepository.save(spendingLog);

        Diary diary = Diary.builder()
                .userId(dto.getUserId())
                .content(dto.getContent())
                .emotionTag(dto.getEmotionTag())
                .sentimentScore(0.0)
                .isImpulsive(isImpulsive)
                .isMain(isMain)
                .isFixed(dto.getIsFixed() != null ? dto.getIsFixed() : false)
                .logId(saveLog.getLogId())
                .regDate(recordDate)
                .build();

        diaryRepository.save(diary);
    }

    public Map<String, Long> getMonthlyTotal(String userId, String month) {
        YearMonth ym = YearMonth.parse(month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<SpendingLog> monthLogs = spendingLogRepository.findMonthlyLogs(userId, startDate, endDate);

        long totalWithFixed = 0;
        long totalWithoutFixed = 0;

        for (SpendingLog log : monthLogs) {
            long amount = (log.getAmount() != null) ? log.getAmount().longValue() : 0L;
            totalWithFixed += amount;

            boolean isFixed = diaryRepository.findByLogId(log.getLogId())
                    .map(Diary::getIsFixed)
                    .orElse(false);

            if (!isFixed) {
                totalWithoutFixed += amount;
            }
        }
        Map<String, Long> result = new HashMap<>();
        result.put("totalWithFixed", totalWithFixed);
        result.put("totalWithoutFixed", totalWithoutFixed);
        return result;
    }

    @Transactional(readOnly = true)
    public List<SpendingLog> getDailyTimeline(String userId) {
        return spendingLogRepository.findByUserIdOrderBySpentAtDesc(userId);
    }

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

    // 🍙 [수정됨] 상세페이지 조회 시 에러 방지
    public SpendingLog getLogDetail(Long logId) {
        return spendingLogRepository.findById(logId)
                .orElseGet(() -> {
                    System.out.println("⚠️ SpendingLog를 찾지 못했습니다. ID: " + logId);
                    return new SpendingLog(); // 에러를 던지는 대신 빈 객체 반환
                });
    }

    public String analyzeText(String content) {
        String url = "http://127.0.0.1:5000/predict";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("text", content);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            if (response != null && response.get("label") != null) {
                String label = String.valueOf(response.get("label"));
                return label.startsWith("1") ? "1" : "0";
            }
            return "0";
        } catch (Exception e) {
            return "0";
        }
    }

    // 🍙 [수정됨] 일기 정보가 없으면 새로 생성해서 반환 (Null 방지)
    public Diary getDiaryByLogId(Long logId){
        return diaryRepository.findByLogId(logId)
                .orElseGet(() -> {
                    Diary newDiary = new Diary();
                    newDiary.setLogId(logId);
                    newDiary.setEmotionTag("😊"); // 기본 이모티콘 설정
                    newDiary.setContent("");
                    return newDiary;
                });
    }

    @Transactional
    public void deleteRecord(Long logId) {
        diaryRepository.deleteByLogId(logId);
        spendingLogRepository.deleteById(logId);
    }

    @Transactional
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
    public void toggleFixedStatus(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("해당 기록을 찾을 수 없습니다."));

        boolean currentStatus = (diary.getIsFixed() != null) ? diary.getIsFixed() : false;
        diary.setIsFixed(!currentStatus);
    }

    private LocalDateTime parseSpentAt(String spentAt) {
        try {
            if (spentAt == null || spentAt.isBlank()) return LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(spentAt, formatter);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    public List<SpendingLog> getTodayRecords(String userId, LocalDate regDate) {
        return spendingLogRepository.findByUserIdAndRegDateOrderBySpentAtDesc(userId, regDate);
    }
}