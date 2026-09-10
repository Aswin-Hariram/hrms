package com.demo.HRMS.Security;

import com.demo.HRMS.Types.EmployeeAuthorities;
import com.demo.HRMS.Types.EmployeeRole;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Repositories.EmployeeRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private EmployeeRepository emp_repo;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");


        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }


        String token = header.substring(7);


        if (!jwtService.validateJWT(token)) {
            filterChain.doFilter(request, response);
            return;
        }


        Long empId = jwtService.extractID(token);
        Long orgId = jwtService.extractOrg(token);

        EmployeeEntity emp = emp_repo.findByEmpIDAndOrganisation_OrgID(empId,orgId)
                .orElseThrow(()-> new RuntimeException("Invalid token"));




        List<GrantedAuthority> authorities= getAuthorities(emp);




        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        empId,
                        token,
                       authorities
                );


        SecurityContextHolder
                .getContext()
                .setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    private List<GrantedAuthority> getAuthorities(EmployeeEntity emp) {

        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(
                new SimpleGrantedAuthority(
                        "ROLE_" + emp.getEmpRole().name()
                )
        );

        if (emp.getEmpRole() == EmployeeRole.SUPER_ADMIN) {
            authorities.add(new SimpleGrantedAuthority(EmployeeAuthorities.CREATE_EMPLOYEE.name()));
        } else if (emp.getEmpRole()==EmployeeRole.HR) {
            authorities.add(new SimpleGrantedAuthority(EmployeeAuthorities.CREATE_EMPLOYEE.name()));
        }


        return authorities;
    }
}

