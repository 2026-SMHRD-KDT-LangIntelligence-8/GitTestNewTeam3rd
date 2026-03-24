package com.smhrd.jumeokbap.repository;

import com.smhrd.jumeokbap.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
}
