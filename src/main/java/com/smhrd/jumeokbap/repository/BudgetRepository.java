package com.smhrd.jumeokbap.repository;

import com.smhrd.jumeokbap.domain.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findTopByUser_UserIdOrderByGoalIdDesc(String userId);
    Optional<Budget> findByUser_UserIdAndIsActiveTrue(String userId);

}

