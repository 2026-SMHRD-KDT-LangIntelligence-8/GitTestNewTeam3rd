package com.smhrd.jumeokbap.service;

import com.smhrd.jumeokbap.domain.Budget;
import com.smhrd.jumeokbap.domain.SpendingLog;
import com.smhrd.jumeokbap.repository.BudgetRepository;
import com.smhrd.jumeokbap.repository.SpendingLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final BudgetRepository budgetRepository;
    private final SpendingLogRepository spendingLogRepository;

    public Map<String, Object> getCalendarData(String userId) {
        Map<String, Object> data = new HashMap<>();

        // 기준 날짜 설정
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

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

        // 충동구매 횟수 계산

        // 날짜별 이모티콘 지도

        return data;
    }

}
