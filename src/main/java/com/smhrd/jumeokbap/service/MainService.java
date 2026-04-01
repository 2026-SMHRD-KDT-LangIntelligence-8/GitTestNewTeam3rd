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
        System.out.println("=== [DEBUG 1] 서비스 진입! ID: [" + userId + "] ===");

        LocalDate todayDate = LocalDate.now();

        // 1. 오늘 사용 금액 계산 (챌린지 유무와 상관없이 항상 계산)
        Long todaySum = spendingLogRepository.sumSpendingByDate(userId, todayDate);
        long todayUsage = (todaySum == null) ? 0L : todaySum;

        System.out.println("=== [DEBUG 2] 오늘 사용 금액: " + todayUsage + " ===");

        // 2. 예산(챌린지) 정보 조회
        Budget budget = budgetRepository
                .findTopByUser_UserIdAndIsActiveTrueOrderByGoalIdDesc(userId)
                .orElse(null);

        // 3. 챌린지가 없을 때도 todayUsage는 정상 출력
        if (budget == null) {
            System.out.println("=== [DEBUG 3] Budget 정보 없음 ===");

            return MainDashboardResponse.builder()
                    .challengeName("진행 중인 챌린지가 없어요")
                    .todayUsage(todayUsage)
                    .bubbleMessage("오늘은 " + String.format("%,d", todayUsage) + "원을 사용했어요!")
                    .progressPercent(0.0)
                    .accumulatedUsage(0L)
                    .overBudget(false)
                    .build();
        }

        System.out.println("=== [DEBUG 4] Budget 확인 성공: " + budget.getChallengeName() + " ===");

        // 4. 챌린지 기간 누적 사용 금액 계산
        LocalDate startDate = budget.getStartDate();
        LocalDate endDate = budget.getEndDate();

        // 오늘 날짜가 챌린지 시작 전이면 누적값은 0
        long accumulatedUsage = 0L;

        if (!todayDate.isBefore(startDate)) {
            LocalDate periodEndDate = todayDate.isAfter(endDate) ? endDate : todayDate;

            Long accumulatedSum = spendingLogRepository.sumSpendingByPeriod(userId, startDate, periodEndDate);
            accumulatedUsage = (accumulatedSum == null) ? 0L : accumulatedSum;
        }

        System.out.println("=== [DEBUG 5] 누적 사용 금액: " + accumulatedUsage + " ===");

        // 5. 예산 관련 값 계산
        long totalLimit = budget.getTotalLimit();
        long dailyLimit = calculateDailyLimit(budget);

        System.out.println("=== [DEBUG 6] 일일 예산: " + dailyLimit + " ===");
        System.out.println("=== [DEBUG 7] 총 예산: " + totalLimit + " ===");

        // 6. 오늘 예산 초과 여부
        boolean overBudget = todayUsage > dailyLimit;

        // 7. 말풍선 메시지
        String message;
        if (overBudget) {
            message = "예산을 초과했어요! 내일은 조금만 아껴써볼까요? 😭";
        } else {
            message = "오늘은 " + String.format("%,d", todayUsage) + "원을 사용했어요!";
        }

        // 8. 진행률 계산 (누적 사용량 기준)
        double progressPercent = (totalLimit > 0)
                ? ((double) accumulatedUsage / totalLimit) * 100
                : 0.0;

        return MainDashboardResponse.builder()
                .challengeName(budget.getChallengeName())
                .todayUsage(todayUsage)
                .bubbleMessage(message)
                .progressPercent(Math.min(100.0, progressPercent))
                .accumulatedUsage(accumulatedUsage)
                .overBudget(overBudget)
                .build();
    }

    private long calculateDailyLimit(Budget budget) {
        long days = ChronoUnit.DAYS.between(budget.getStartDate(), budget.getEndDate()) + 1;

        if (days <= 0) {
            return 0L;
        }

        long fixedCostSum = (budget.getFixedCostSum() != null) ? budget.getFixedCostSum() : 0L;
        long availableBudget = budget.getTotalLimit() - fixedCostSum;

        if (availableBudget <= 0) {
            return 0L;
        }

        return availableBudget / days;
    }
}