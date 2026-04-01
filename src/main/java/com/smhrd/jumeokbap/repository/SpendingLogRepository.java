package com.smhrd.jumeokbap.repository;

import com.smhrd.jumeokbap.domain.SpendingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SpendingLogRepository extends JpaRepository<SpendingLog, Long> {

    List<SpendingLog> findByUserIdOrderBySpentAtDesc(String userId);

    List<SpendingLog> findByUserIdAndRegDate(String userId, LocalDate regDate);

    List<SpendingLog> findByUserIdAndRegDateOrderBySpentAtDesc(String userId, LocalDate regDate);

    List<SpendingLog> findByUserIdAndRegDateBetween(String userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT s FROM SpendingLog s WHERE s.userId = :userId AND s.regDate >= :startDate AND s.regDate <= :endDate")
    List<SpendingLog> findMonthlyLogs(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 특정 유저가 특정 날짜에 쓴 금액 총합
    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM SpendingLog s WHERE s.userId = :userId AND s.regDate = :regDate")
    Long sumSpendingByDate(@Param("userId") String userId,
                           @Param("regDate") LocalDate regDate);

    // 특정 유저가 특정 기간 동안 쓴 금액 총합
    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM SpendingLog s WHERE s.userId = :userId AND s.regDate BETWEEN :startDate AND :endDate")
    Long sumSpendingByPeriod(@Param("userId") String userId,
                             @Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate);

    // 자동 동기화 시 중복 저장 방지
    boolean existsByUserIdAndRegDateAndSpentAtAndAmountAndStoreName(
            String userId,
            LocalDate regDate,
            LocalDateTime spentAt,
            Integer amount,
            String storeName
    );

    void deleteByUserId(String userId);
}