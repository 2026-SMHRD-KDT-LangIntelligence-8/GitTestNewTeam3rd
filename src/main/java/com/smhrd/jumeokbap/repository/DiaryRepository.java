package com.smhrd.jumeokbap.repository;

import com.smhrd.jumeokbap.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    void deleteByLogId(Long logId);
    Optional<Diary> findByLogId(Long logId);
    boolean existsByUserIdAndRegDate(String userId, LocalDate regDate);
}
