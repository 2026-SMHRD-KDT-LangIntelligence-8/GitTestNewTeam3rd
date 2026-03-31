package com.smhrd.jumeokbap.service;

import com.smhrd.jumeokbap.domain.UserCodefAccount;
import com.smhrd.jumeokbap.dto.CodefConnectedIdRequest;
import com.smhrd.jumeokbap.repository.UserCodefAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCodefAccountService {

    private final UserCodefAccountRepository repository;

    public void saveConnectedId(String userId, String connectedId, CodefConnectedIdRequest dto) {

        UserCodefAccount entity = repository
                .findByUserIdAndOrganizationAndBusinessType(
                        userId,
                        dto.getOrganization(),
                        "CD"
                )
                .orElse(new UserCodefAccount());

        entity.setUserId(userId);
        entity.setConnectedId(connectedId);
        entity.setOrganization(dto.getOrganization());
        entity.setBusinessType("CD");
        entity.setClientType("P");
        entity.setLoginType("1");
        entity.setLoginId(dto.getLoginId());
        entity.setAccountAlias(dto.getAccountAlias());

        repository.save(entity);
    }

    // 연결된 카드 계정 목록 조회
    public List<UserCodefAccount> getAccounts(String userId) {
        return repository.findByUserId(userId);
    }

    // 연결된 카드 계정 삭제
    public void deleteAccount(Long id) {
        repository.deleteById(id);
    }
}