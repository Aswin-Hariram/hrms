package com.demo.HRMS.Security;


import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Repositories.EmployeeRepository;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET;
    private  final  long EXPIRATION = 1000*60 * 60*24;






    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );
    }



    public String genToken(EmployeeEntity employee){
        return Jwts
                .builder()
                .claim("emp_id",employee.getEmpID())
                .claim("org_id",employee.getOrganisation().getOrgID())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }
    public boolean validateJWT(String token){

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

    public String extractRole(String token){
        return  Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("emp_role",String.class);
    }
    public Long extractOrg(String token){
        return  Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("org_id",Long.class);
    }

    public Long extractID(String token){
        return  Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("emp_id", Long.class);
    }

}

