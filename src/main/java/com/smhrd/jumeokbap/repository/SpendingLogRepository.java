package com.smhrd.jumeokbap.repository;

import com.smhrd.jumeokbap.domain.SpendingLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SpendingLogRepository extends JpaRepository<SpendingLog, Long> {

    List<SpendingLog> findByUserIdOrderBySpentAtDesc(String userId);
    List<SpendingLog> findByUserIdAndRegDate(String userId, String regDate);
    List<SpendingLog> findByUserIdAndRegDateBetween(String userId, LocalDate startDate, LocalDate endDate);
}
