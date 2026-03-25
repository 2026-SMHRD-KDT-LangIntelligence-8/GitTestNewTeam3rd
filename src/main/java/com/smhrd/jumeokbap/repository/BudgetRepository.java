package com.smhrd.jumeokbap.repository;

import com.smhrd.jumeokbap.domain.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    // JPA가 저장, 수정, 삭제 기능 다 만들어줌! 코드 작성 안 해도 됨!
}
