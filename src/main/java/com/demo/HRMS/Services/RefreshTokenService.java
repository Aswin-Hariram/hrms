package com.demo.HRMS.Services;

import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.RefreshTokenEntity;
import com.demo.HRMS.Repositories.RefreshTokenRepository;
import com.demo.HRMS.Security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtService jwtService;

    @Transactional
    public RefreshTokenEntity createRefreshToken(EmployeeEntity emp) {

        refreshTokenRepository.revokeAllForEmployee(
                emp.getEmpID(), emp.getOrganisation().getOrgID());

        RefreshTokenEntity rt = new RefreshTokenEntity();
        rt.setToken(jwtService.genRefreshTokenValue());
        rt.setEmpId(emp.getEmpID());
        rt.setOrgId(emp.getOrganisation().getOrgID());
        rt.setExpiryDate(Instant.now().plusMillis(jwtService.getRefreshExpirationMillis()));
        rt.setRevoked(false);
        return refreshTokenRepository.save(rt);
    }

    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public boolean isExpired(RefreshTokenEntity rt) {
        return rt.getExpiryDate().isBefore(Instant.now());
    }

    @Transactional
    public void revoke(RefreshTokenEntity rt) {
        rt.setRevoked(true);
        refreshTokenRepository.save(rt);
    }
}