package com.smhrd.jumeokbap.service;

import com.smhrd.jumeokbap.domain.Budget;
import com.smhrd.jumeokbap.domain.User;
import com.smhrd.jumeokbap.dto.BudgetRequest;
import com.smhrd.jumeokbap.repository.BudgetRepository;
import com.smhrd.jumeokbap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    // 예산 설정 저장
    public void saveBudget(BudgetRequest budgetRequest) {
        // 1. findByUserId 대신 JPA 기본 메서드인 findById를 사용합니다.
        // User 엔터티의 @Id가 userId이므로 findById가 그 역할을 수행합니다.
        User user = userRepository.findById(budgetRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다. ID: " + budgetRequest.getUserId()));

        // 2. 주인(user)이 확실히 매핑된 Budget 객체 생성
        Budget budget = Budget.builder()
                .challengeName(budgetRequest.getChallengeName())
                .startDate(budgetRequest.getStartDate())
                .endDate(budgetRequest.getEndDate())
                .totalLimit(budgetRequest.getTotalLimit())
                .fixedCostSum(budgetRequest.getFixedCostSum())
                .isActive(true) // 기본으로 활성화
                .user(user)     // 연결 완료! 🍙
                .build();

        // 3. DB 저장
        budgetRepository.save(budget);
    }

}
