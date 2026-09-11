package com.demo.HRMS.Controllers;

import com.demo.HRMS.DTO.Auth.RefreshRequest;
import com.demo.HRMS.DTO.Auth.TokenResponse;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.RefreshTokenEntity;
import com.demo.HRMS.Repositories.EmployeeRepository;
import com.demo.HRMS.Security.JwtService;

import com.demo.HRMS.Services.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private JwtService jwtService;
    @Autowired private RefreshTokenService refreshTokenService;
    @Autowired private EmployeeRepository empRepo;


    public TokenResponse issueTokens(EmployeeEntity emp) {
        String access = jwtService.genAccessToken(emp);
        RefreshTokenEntity rt = refreshTokenService.createRefreshToken(emp);
        return new TokenResponse(access, rt.getToken(), 15 * 60);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest req) {
        return refreshTokenService.findByToken(req.getRefreshToken())
                .map(rt -> {
                    if (rt.isRevoked() || refreshTokenService.isExpired(rt)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("Refresh token expired or revoked");
                    }
                    EmployeeEntity emp = empRepo
                            .findByEmpIDAndOrganisation_OrgID(rt.getEmpId(), rt.getOrgId())
                            .orElse(null);
                    if (emp == null || emp.getEmpStatus() == null || !emp.getEmpStatus().isActive()) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
                    }


                    refreshTokenService.revoke(rt);
                    RefreshTokenEntity newRt = refreshTokenService.createRefreshToken(emp);

                    String newAccess = jwtService.genAccessToken(emp);
                    return ResponseEntity.ok(new TokenResponse(newAccess, newRt.getToken(), 15 * 60));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshRequest req) {
        refreshTokenService.findByToken(req.getRefreshToken()).
            ifPresent(rt -> refreshTokenService.revoke(rt));
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message","Logged out"));
    }
}