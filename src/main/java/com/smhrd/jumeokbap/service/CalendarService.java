package com.smhrd.jumeokbap.service;

import com.smhrd.jumeokbap.domain.Budget;
import com.smhrd.jumeokbap.domain.Diary;
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

    public Map<String, Object> getCalendarData(String userId) {
        Map<String, Object> data = new HashMap<>();

        // 기준 날짜 설정
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        //
        System.out.println("조회 시작일: " + startDate);
        System.out.println("조회 종료일: " + endDate);

        // 소비 목표
        Optional<Budget> budget = budgetRepository.findByUser_UserIdAndIsActiveTrue(userId);
        data.put("totalAmount", budget.map(Budget::getTotalLimit).orElse(0L));

        // 이번 달 지출 내역
        List<SpendingLog> monthLogs = spendingLogRepository.findByUserIdAndRegDateBetween(userId, startDate, endDate);

        // 지출 합계 계산
        long totalSpent = monthLogs.stream()
                .mapToLong(log -> log.getAmount() != null ? log.getAmount().longValue() : 0L)
                .sum();

        Map<String, String> dailyEmojis = new HashMap<>();
        AtomicInteger impulseCount = new AtomicInteger(0);

        for (SpendingLog log : monthLogs) {
            int day = log.getRegDate().getDayOfMonth();
            String dayKey = String.valueOf(day);

            dailyEmojis.putIfAbsent(dayKey, "💰");

            diaryRepository.findByLogId(log.getLogId()).ifPresent(diary -> {
                if (Boolean.TRUE.equals(diary.getIsImpulsive())) {
                    impulseCount.incrementAndGet();
                }

                // 🍙 수정 포인트: 비교와 저장 모두 dayKey 사용
                if (Boolean.TRUE.equals(diary.getIsMain()) || "💰".equals(dailyEmojis.get(dayKey))) {
                    if (diary.getEmotionTag() != null) {
                        dailyEmojis.put(dayKey, diary.getEmotionTag());
                            }
                        }
            });
        }

        // 결과 데이터 맵에 담기
        data.put("totalSpent",totalSpent);
        data.put("daysInMonth", now.lengthOfMonth());
        data.put("impulseCount", impulseCount.get());
        data.put("dailyEmojis", dailyEmojis);

        return data;
    }

}
