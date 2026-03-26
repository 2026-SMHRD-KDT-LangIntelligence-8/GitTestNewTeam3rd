package com.smhrd.jumeokbap.repository;

import com.smhrd.jumeokbap.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Optional<Diary> findByLogId(Long logId);
}
