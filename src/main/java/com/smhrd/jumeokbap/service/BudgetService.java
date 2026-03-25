package com.smhrd.jumeokbap.service;

import com.smhrd.jumeokbap.domain.Budget;
import com.smhrd.jumeokbap.dto.BudgetRequest;
import com.smhrd.jumeokbap.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetService {
    private  final BudgetRepository budgetRepository;

    // 예산 설정 저장
    public void saveBudget(BudgetRequest budgetRequest) {
        Budget budget = Budget.builder()
                .challengeName(budgetRequest.getChallengeName())
                .startDate(budgetRequest.getStartDate())
                .endDate(budgetRequest.getEndDate())
                .totalLimit(budgetRequest.getTotalLimit())
                .fixedCostSum(budgetRequest.getFixedCostSum())
                .isActive(true) // 기본으로 활성화 상태
                // .user(user) // 만약 유저 정보도 넣어야 한다면 주석 해제!
                .build();

        budgetRepository.save(budget);
    }

}
