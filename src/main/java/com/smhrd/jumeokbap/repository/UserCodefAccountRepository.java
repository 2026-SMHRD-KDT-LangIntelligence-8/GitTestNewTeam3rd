package com.smhrd.jumeokbap.repository;

import com.smhrd.jumeokbap.domain.UserCodefAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCodefAccountRepository extends JpaRepository<UserCodefAccount, Long> {

    List<UserCodefAccount> findByUserId(String userId);

    Optional<UserCodefAccount> findByUserIdAndOrganization(String userId, String organization);
}