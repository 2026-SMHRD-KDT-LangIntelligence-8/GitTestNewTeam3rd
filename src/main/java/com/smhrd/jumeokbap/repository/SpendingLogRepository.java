package com.smhrd.jumeokbap.repository;

import com.smhrd.jumeokbap.domain.SpendingLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SpendingLogRepository extends JpaRepository<SpendingLog, Long> {

    List<SpendingLog> findByUserIdOrderBySpentAtDesc(String userId);
    List<SpendingLog> findByUserIdAndRegDate(String userId, LocalDate regDate);
    List<SpendingLog> findByUserIdAndRegDateBetween(String userId, LocalDate startDate, LocalDate endDate);

    // 특정 유저(userId)가 특정 날짜(date)에 쓴 금액의 총합
    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM SpendingLog s WHERE s.userId = :userId AND s.regDate = :regDate")
    Long sumSpendingByDate(@Param("userId") String userId, @Param("regDate") LocalDate regDate);
}
