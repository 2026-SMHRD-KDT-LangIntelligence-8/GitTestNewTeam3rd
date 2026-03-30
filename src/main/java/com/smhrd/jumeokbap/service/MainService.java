package com.smhrd.jumeokbap.service;

import com.smhrd.jumeokbap.domain.Budget;
import com.smhrd.jumeokbap.dto.MainDashboardResponse;
import com.smhrd.jumeokbap.repository.BudgetRepository;
import com.smhrd.jumeokbap.repository.SpendingLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class MainService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private SpendingLogRepository spendingLogRepository;

    public MainDashboardResponse getMainData(String userId) {
        System.out.println("=== [DEBUG 1] 서비스 진입! 유저 ID: [" + userId + "] ===");

        String today = LocalDate.now().toString();

        // 위에서 새로 만든 findLatestBudget 메서드를 사용합니다.
        Budget budget = budgetRepository.findLatestBudget(userId).orElse(null);

        if (budget == null) {
            System.out.println("=== [DEBUG 2] Budget이 null입니다. DB를 확인하세요! ===");
            return MainDashboardResponse.builder()
                    .challengeName("진행 중인 챌린지가 없어요")
                    .todayUsage(0L)
                    .bubbleMessage("새로운 목표를 설정해볼까요?")
                    .progressPercent(0.0)
                    .isOverBudget(false)
                    .build();
        }

        System.out.println("=== [DEBUG 3] budget 확인 성공! 챌린지명: " + budget.getChallengeName());

        // 오늘 사용 금액 조회
        Long todaySum = spendingLogRepository.sumTodaySpending(userId, today);
        long todayUsage = (todaySum == null) ? 0L : todaySum;

        // 누적 사용량 (우선 오늘 사용량으로 세팅)
        long accumulatedUsage = todayUsage;
        long totalLimit = budget.getTotalLimit();

        // 하루 권장 예산 계산
        long dailyLimit = calculateDailyLimit(budget);

        // 초과 여부 및 메시지
        boolean isOver = todayUsage > dailyLimit;
        String message = isOver ? "예산을 초과했어요! 😭" : "오늘 " + String.format("%,d", todayUsage) + "원 사용 중! 🍙";

        double progress = (totalLimit > 0) ? ((double) accumulatedUsage / totalLimit) * 100 : 0;

        return new MainDashboardResponse(
                budget.getChallengeName(),
                todayUsage,
                message,
                Math.min(100.0, progress),
                accumulatedUsage,
                isOver
        );
    }

    private long calculateDailyLimit(Budget budget) {
        long days = ChronoUnit.DAYS.between(budget.getStartDate(), budget.getEndDate()) + 1;
        if (days <= 0) return 0;

        long fixed = (budget.getFixedCostSum() != null) ? budget.getFixedCostSum() : 0L;
        return (budget.getTotalLimit() - fixed) / days;
    }
}
