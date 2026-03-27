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

        // 이번 달 지출 기록
        List<SpendingLog> monthLogs = spendingLogRepository.findByUserIdAndRegDateBetween(userId, startDate, endDate);

        return data;
    }

}
