package com.smhrd.jumeokbap.repository;

import com.smhrd.jumeokbap.domain.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    // 가장 최근 예산 1개 조회
    Optional<Budget> findTopByUser_UserIdOrderByGoalIdDesc(String userId);

    // 활성화된 예산 중 가장 최근 1개 조회
    Optional<Budget> findTopByUser_UserIdAndIsActiveTrueOrderByGoalIdDesc(String userId);

    void deleteByUser_UserId(String userId);
}