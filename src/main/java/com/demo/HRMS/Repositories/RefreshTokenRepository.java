package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshTokenEntity r SET r.revoked = true WHERE r.empId = :empId AND r.orgId = :orgId")
    void revokeAllForEmployee(Long empId, Long orgId);
}