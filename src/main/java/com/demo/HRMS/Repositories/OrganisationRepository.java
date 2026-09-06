package com.demo.HRMS.Repositories;


import com.demo.HRMS.Entities.OrganisationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganisationRepository extends JpaRepository<OrganisationEntity, Long> {


    boolean existsByOrgEmail(String email);
}