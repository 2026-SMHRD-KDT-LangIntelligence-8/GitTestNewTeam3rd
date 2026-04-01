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

        //  이미지 파일
        String savedPath = ""; // DB에 저장할 경로 변수
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // 프로젝트 내의 static/uploads 폴더 경로 설정
                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";

                // 파일명 중복 방지를 위해 UUID나 타임스탬프 추가 (선택사항이지만 권장)
                String fileName = java.util.UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                java.io.File saveFile = new java.io.File(uploadDir + fileName);

                // 폴더가 없으면 생성
                if (!saveFile.getParentFile().exists()) {
                    saveFile.getParentFile().mkdirs();
                }

                // 실제 폴더에 파일 저장
                imageFile.transferTo(saveFile);

                // DB에는 웹에서 접근 가능한 상대 경로로 저장
                savedPath = "/uploads/" + fileName;

            } catch (java.io.IOException e) {
                e.printStackTrace();
                // 실패 시 빈 값 처리 혹은 예외 발생
            }
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
                .imageUrl(savedPath)
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
        // 1. [추가] 삭제 전 DB에서 해당 기록의 이미지 경로를 가져옵니다.
        spendingLogRepository.findById(logId).ifPresent(log -> {
            String imageUrl = log.getImageUrl();

            // 2. [추가] 이미지 경로가 존재한다면 실제 파일 삭제 진행
            if (imageUrl != null && !imageUrl.isEmpty()) {
                try {
                    // 저장할 때와 동일한 경로 설정
                    String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static";
                    java.io.File file = new java.io.File(uploadDir + imageUrl);

                    if (file.exists()) {
                        if (file.delete()) {
                            System.out.println("✅ 실제 이미지 파일 삭제 성공: " + imageUrl);
                        } else {
                            System.out.println("⚠️ 이미지 파일 삭제 실패");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("❌ 파일 삭제 중 에러 발생");
                    e.printStackTrace();
                }
            }
        });

        // 3. 기존 DB 삭제 로직 진행
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

}