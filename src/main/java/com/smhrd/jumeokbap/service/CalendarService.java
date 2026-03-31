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

    public Map<String, Object> getCalendarData(String userId) {
        Map<String, Object> data = new HashMap<>();

        // 기준 날짜 설정
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        System.out.println("조회 시작일: " + startDate);
        System.out.println("조회 종료일: " + endDate);

        // 소비 목표
        Optional<Budget> budget = budgetRepository.findByUser_UserIdAndIsActiveTrue(userId);
        data.put("totalAmount", budget.map(Budget::getTotalLimit).orElse(0L));

        // 이번 달 지출 내역
        List<SpendingLog> monthLogs = spendingLogRepository.findByUserIdAndRegDateBetween(userId, startDate, endDate);

        // 총 합계 계산
        long totalSpent = monthLogs.stream()
                .mapToLong(SpendingLog::getAmount)
                .sum();
        data.put("totalSpent", totalSpent);

        // 달력에 필요한 날짜 수
        data.put("daysInMonth", now.lengthOfMonth());

        Map<Integer, String> dailyEmojis = new HashMap<>();
        AtomicInteger impulseCount = new AtomicInteger();

        for (SpendingLog log : monthLogs) {
            diaryRepository.findByLogId(log.getLogId()).ifPresent(diary -> {

                // 충동구매 횟수 카운트
                if (diary.getIsImpulsive()) {
                     impulseCount.getAndIncrement();
                }
                // 대표 지출의 감정 이모티콘
                if (diary.getIsMain()) {
                    int day = log.getRegDate().getDayOfMonth();
                    dailyEmojis.put(day, diary.getEmotionTag());
                }
            });
        }

        // 결과 데이터 맵에 담기
        data.put("impulseCount", impulseCount);
        data.put("dailyEmojis", dailyEmojis);

        return data;
    }

}
