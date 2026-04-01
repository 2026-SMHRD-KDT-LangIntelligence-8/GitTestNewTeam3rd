package com.smhrd.jumeokbap.repository;

import com.smhrd.jumeokbap.domain.UserCodefAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCodefAccountRepository extends JpaRepository<UserCodefAccount, Long> {

    // 특정 사용자 + 기관 + 타입의 가장 최근 connectedId 1개 조회
    Optional<UserCodefAccount> findTopByUserIdAndOrganizationAndBusinessTypeOrderByIdDesc(
            String userId, String organization, String businessType
    );

    // 특정 사용자의 전체 connectedId 목록 조회
    List<UserCodefAccount> findByUserId(String userId);

    // 특정 사용자 + 기관 + 타입 전체 조회 (필요할 때 사용)
    List<UserCodefAccount> findByUserIdAndOrganizationAndBusinessType(
            String userId, String organization, String businessType
    );

    void deleteByUserId(String userId);
}