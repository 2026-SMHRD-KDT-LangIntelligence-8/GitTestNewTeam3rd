package com.smhrd.jumeokbap.service;

import com.smhrd.jumeokbap.domain.Budget;
import com.smhrd.jumeokbap.domain.SpendingLog;
import com.smhrd.jumeokbap.repository.BudgetRepository;
import com.smhrd.jumeokbap.repository.DiaryRepository;
import com.smhrd.jumeokbap.repository.SpendingLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final BudgetRepository budgetRepository;
    private final SpendingLogRepository spendingLogRepository;
    private final DiaryRepository diaryRepository;

    /**
     * 🍙 수정된 메서드: targetDate를 파라미터로 받습니다.
     */
    public Map<String, Object> getCalendarData(String userId, LocalDate targetDate) {
        Map<String, Object> data = new HashMap<>();

        // 1. 기준 날짜 설정 (이제 now 대신 파라미터로 받은 targetDate를 사용합니다)
        LocalDate startDate = targetDate.withDayOfMonth(1);
        LocalDate endDate = targetDate.withDayOfMonth(targetDate.lengthOfMonth());

        System.out.println("--- 캘린더 데이터 조회 시작 ---");
        System.out.println("사용자 ID: " + userId);
        System.out.println("조회 범위: " + startDate + " ~ " + endDate);

        // 2. 소비 목표 조회
        Optional<Budget> budget = budgetRepository.findTopByUser_UserIdAndIsActiveTrueOrderByGoalIdDesc(userId);
        data.put("totalAmount", budget.map(Budget::getTotalLimit).orElse(0L));

        // 3. 해당 월의 지출 내역 조회
        List<SpendingLog> monthLogs = spendingLogRepository.findByUserIdAndRegDateBetween(userId, startDate, endDate);

        // 4. 지출 합계 계산
        long totalSpent = monthLogs.stream()
                .mapToLong(log -> log.getAmount() != null ? log.getAmount().longValue() : 0L)
                .sum();

        Map<String, String> dailyEmojis = new HashMap<>();
        AtomicInteger impulseCount = new AtomicInteger(0);

        // 5. 지출 로그별 이모티콘 및 충동구매 분석
        for (SpendingLog log : monthLogs) {
            int day = log.getRegDate().getDayOfMonth();
            String dayKey = String.valueOf(day);

            // 해당 날짜에 데이터가 있으면 일단 기본 돈주머니 설정
            dailyEmojis.putIfAbsent(dayKey, "💰");

            // 일기(Diary) 테이블에서 감정 태그 확인
            diaryRepository.findByLogId(log.getLogId()).ifPresent(diary -> {
                // 충동구매 여부 체크
                if (Boolean.TRUE.equals(diary.getIsImpulsive())) {
                    impulseCount.incrementAndGet();
                }

                // 대표 지출이거나 아직 기본 이모티콘인 경우에만 감정 태그로 업데이트
                if (Boolean.TRUE.equals(diary.getIsMain()) || "💰".equals(dailyEmojis.get(dayKey))) {
                    if (diary.getEmotionTag() != null) {
                        dailyEmojis.put(dayKey, diary.getEmotionTag());
                    }
                }
            });
        }

        // 6. 결과 데이터 담기 (HTML 변수명과 맞춤)
        data.put("totalSpent", totalSpent);
        data.put("daysInMonth", targetDate.lengthOfMonth());
        data.put("currentYear", targetDate.getYear());    // HTML 상단 연도 표시용
        data.put("currentMonth", targetDate.getMonthValue()); // HTML 상단 월 표시용
        data.put("impulseCount", impulseCount.get());
        data.put("dailyEmojis", dailyEmojis);

        return data;
    }
}