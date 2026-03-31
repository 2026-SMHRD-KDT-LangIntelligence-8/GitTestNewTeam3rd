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

        Map<Integer, String> dailyEmojis = new HashMap<>();
        AtomicInteger impulseCount = new AtomicInteger(0);

        for (SpendingLog log : monthLogs) {
            int day = log.getRegDate().getDayOfMonth();

            // 🍙 3. 추가한 곳: 자동 데이터(Diary 없는 경우)를 위해 기본 💰 설정
            if (!dailyEmojis.containsKey(day)) {
                dailyEmojis.put(day, "💰");
            }

            diaryRepository.findByLogId(log.getLogId()).ifPresent(diary -> {

                // 🍙 4. 수정한 곳: 충동구매 카운트 로직을 이 안에 넣습니다.
                if (Boolean.TRUE.equals(diary.getIsImpulsive())) {
                    impulseCount.incrementAndGet();
                }

                // 🍙 5. 수정한 곳: 이모티콘 로직 (대표 지출이거나 아직 기본값인 경우 덮어쓰기)
                if (Boolean.TRUE.equals(diary.getIsMain()) || "💰".equals(dailyEmojis.get(day))) {
                    dailyEmojis.put(day, diary.getEmotionTag());
                }
            });
        }

        // 결과 데이터 맵에 담기
        data.put("totalSpent",totalSpent);
        data.put("daysInMonth", now.lengthOfMonth());
        data.put("impulseCount", impulseCount);
        data.put("dailyEmojis", dailyEmojis);

        return data;
    }

}
