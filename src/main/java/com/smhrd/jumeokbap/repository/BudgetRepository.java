package com.smhrd.jumeokbap.repository;

import com.smhrd.jumeokbap.domain.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    // 직접 쿼리를 작성해서 가장 최근의 예산 설정을 가져옵니다.
    // b.user.userId 부분은 User 엔터티의 필드명과 일치해야 합니다!
    @Query("SELECT b FROM Budget b WHERE b.user.userId = :userId ORDER BY b.goalId DESC LIMIT 1")
    Optional<Budget> findLatestBudget(@Param("userId") String userId);

    Optional<Budget> findByUser_UserIdAndIsActiveTrue(String userId);
}

