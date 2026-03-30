package com.smhrd.jumeokbap.service;

import com.smhrd.jumeokbap.domain.Budget;
import com.smhrd.jumeokbap.dto.MainDashboardResponse;
import com.smhrd.jumeokbap.repository.BudgetRepository;
import com.smhrd.jumeokbap.repository.SpendingLogRepository; // 추가됨
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class MainService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private SpendingLogRepository spendingLogRepository; // 추가됨

    public MainDashboardResponse getMainData(String userId) {

        // 오늘 날짜 구하기
        String today = LocalDate.now().toString();

        // 챌린지 정보 가져오기
        Budget budget = budgetRepository.findTopByUser_UserIdOrderByGoalIdDesc(userId)
                .orElse(null);

        // 만약 데이터가 없으면 기본값(Empty State) 반환
        if (budget == null) {
            return MainDashboardResponse.builder()
                    .challengeName("진행 중인 챌린지가 없어요")
                    .todayUsage(0L)
                    .bubbleMessage("새로운 목표를 설정해볼까요?")
                    .progressPercent(0.0)
                    .isOverBudget(false)
                    .build();
        }

        // 2. 오늘 사용 금액 계산

        // 오늘 사용 금액: SpendingLog에서 해당 userId와 오늘 날짜의 amount를 모두 합산함
        Long todaySum = spendingLogRepository.sumSpendingByDate(userId, LocalDate.now());
        long todayUsage = (todaySum == null) ? 0L : todaySum; // 기록 없으면 0원

        // 누적 사용량: 일단 오늘 사용량으로 표시 (전체 누적 쿼리는 추후 추가 가능)
        long accumulatedUsage = todayUsage;

        long totalLimit = budget.getTotalLimit();


        // 3. 하루 권장 예산 계산 로직
        long dailyLimit = calculateDailyLimit(budget);

        // 4. 주먹밥 말풍선 메시지 결정
        String message;
        boolean isOver = todayUsage > dailyLimit;

        if (isOver) {
            message = "내일은 소비를 줄이는 게 좋겠어요!";
        } else {
            message = "오늘 " + String.format("%,d", todayUsage) + "원 썼어요!";
        }

// 5. GOAL 바 진행률 계산
        double progress = (totalLimit > 0) ? ((double) accumulatedUsage / totalLimit) * 100 : 0;

        // 6. DTO에 담아서 반환 (Response)
        return new MainDashboardResponse(
                budget.getChallengeName(), // GOAL 자리에 들어갈 이름
                todayUsage,                // 오늘 사용 금액
                message,                   // 먹밥이 말풍선 메시지
                Math.min(100.0, progress), // 목표 대비 누적 사용량 (0 ~ 100 (%))
                accumulatedUsage,          // 챌린지 시작부터 현재까지 누적 지출액
                isOver                     // 오늘 예산 초과 여부
        );
    }

    // 내부 계산용: 하루 권장 예산 구하기
    private long calculateDailyLimit(Budget budget) {
        long days = ChronoUnit.DAYS.between(budget.getStartDate(), budget.getEndDate()) + 1;
        if (days <= 0) return 0;
        return (budget.getTotalLimit() - (budget.getFixedCostSum() != null ? budget.getFixedCostSum() : 0L)) / days;
    }
}
