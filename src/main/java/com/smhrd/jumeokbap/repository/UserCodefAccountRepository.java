package com.smhrd.jumeokbap.repository;

import com.smhrd.jumeokbap.domain.UserCodefAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCodefAccountRepository extends JpaRepository<UserCodefAccount, Long> {

    Optional<UserCodefAccount> findByUserIdAndOrganizationAndBusinessType(
            String userId, String organization, String businessType
    );

    List<UserCodefAccount> findByUserId(String userId);
}