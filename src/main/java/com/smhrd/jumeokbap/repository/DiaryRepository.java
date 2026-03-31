package com.smhrd.jumeokbap.repository;

import com.smhrd.jumeokbap.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    void deleteByLogId(Long logId);
    Optional<Diary> findByLogId(Long logId);
    List<Diary> findByUserIdAndRegDate(String userId, LocalDate regDate);
    boolean existsByUserIdAndRegDate(String userId, LocalDate regDate);
    List<Diary> findByUserIdAndRegDateBetween(String userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT d FROM Diary d WHERE d.userId = :userId AND d.regDate BETWEEN :startDate AND :endDate")
    List<Diary> findMonthlyDiaries(@Param("userId") String userId,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    void deleteByUserId(String userId);
}
