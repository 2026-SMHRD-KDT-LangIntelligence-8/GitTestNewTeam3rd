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

        // 1. 날짜 타입 변경 (String -> LocalDate)
        LocalDate todayDate = LocalDate.now();

        // 2. 챌린지 정보 가져오기 (새로 만든 findLatestBudget 사용)
        Budget budget = budgetRepository.findLatestBudget(userId).orElse(null);

        if (budget == null) {
            System.out.println("=== [DEBUG 2] Budget 정보 없음 ===");
            return MainDashboardResponse.builder()
                    .challengeName("진행 중인 챌린지가 없어요")
                    .todayUsage(0L)
                    .bubbleMessage("새로운 목표를 설정해볼까요?")
                    .progressPercent(0.0)
                    .isOverBudget(false)
                    .build();
        }

        System.out.println("=== [DEBUG 3] Budget 확인 성공: " + budget.getChallengeName());

        // 3. 오늘 사용 금액 조회 (리포지토리 변경에 맞춰 메서드명과 파라미터 수정)
        Long todaySum = spendingLogRepository.sumSpendingByDate(userId, todayDate);
        long todayUsage = (todaySum == null) ? 0L : todaySum;

        // 4. 누적 사용량 및 예산 데이터 세팅
        long totalLimit = budget.getTotalLimit();
        long dailyLimit = calculateDailyLimit(budget);

        // 5. 초과 여부 및 말풍선 메시지 결정
        boolean isOver = todayUsage > dailyLimit;
        String message;

        if (isOver) {
            message = "예산을 초과했어요! 내일은 조금만 아껴써볼까요? 😭";
        } else {
            message = "오늘 " + String.format("%,d", todayUsage) + "원 사용 중! 아주 잘하고 있어요 🍙";
        }

        // 6. 진행률 계산
        double progress = (totalLimit > 0) ? ((double) todayUsage / totalLimit) * 100 : 0;

        return new MainDashboardResponse(
                budget.getChallengeName(),
                todayUsage,
                message,
                Math.min(100.0, progress),
                todayUsage, // 현재 리포지토리 한계로 오늘 사용량을 누적량으로 우선 표시
                isOver
        );
    }

    private long calculateDailyLimit(Budget budget) {
        // 시작일부터 종료일까지의 총 일수 계산
        long days = ChronoUnit.DAYS.between(budget.getStartDate(), budget.getEndDate()) + 1;
        if (days <= 0) return 0;

        // (총 예산 - 고정 지출) / 총 일수
        long fixed = (budget.getFixedCostSum() != null) ? budget.getFixedCostSum() : 0L;
        return (budget.getTotalLimit() - fixed) / days;
    }
}
