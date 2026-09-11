package com.demo.HRMS.Security;

import com.demo.HRMS.Entities.EmployeeEntity;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET;


    private final long ACCESS_EXPIRATION  = 1000L * 60 * 15;


    private final long REFRESH_EXPIRATION = 1000L * 60 * 60 * 24 * 7;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }


    public String genAccessToken(EmployeeEntity employee) {
        return Jwts.builder()
                .claim("emp_id", employee.getEmpID())
                .claim("org_id", employee.getOrganisation().getOrgID())
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }


    public String genRefreshTokenValue() {
        return UUID.randomUUID().toString() + UUID.randomUUID().toString();
    }

    public long getRefreshExpirationMillis() {
        return REFRESH_EXPIRATION;
    }


    public boolean validateJWT(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long extractOrg(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token).getPayload().get("org_id", Long.class);
    }

    public Long extractID(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token).getPayload().get("emp_id", Long.class);
    }
}