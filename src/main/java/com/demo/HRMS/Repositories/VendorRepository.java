package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.VendorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<VendorEntity, Long> {


    boolean existsByOrganisation_OrgIDAndEmail(Long orgId, String email);
    boolean existsByOrganisation_OrgIDAndVendorName(Long orgId, String vendorName);
    boolean existsByOrganisation_OrgIDAndGstNumber(Long orgId, String gstNumber);


    Optional<VendorEntity> findByIdAndOrganisation_OrgID(Long id, Long orgId);
    List<VendorEntity> findAllByOrganisation_OrgID(Long orgId);
    List<VendorEntity> findAllByOrganisation_OrgIDAndIsActive(Long orgId, Boolean isActive);
}