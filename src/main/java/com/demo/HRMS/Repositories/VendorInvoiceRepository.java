package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.VendorInvoice;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorInvoiceRepository extends JpaRepository<VendorInvoice, Long> {

    Optional<VendorInvoice> findByIdAndOrganisation_OrgID(Long id, Long orgId);


    boolean existsByOrganisation_OrgIDAndInvoiceNumber(Long organisationOrgID, String invoiceNumber);

    List<VendorInvoice> findByOrganisation_OrgIDOrderByInvoiceDateDesc(Long organisationOrgID);
    List<VendorInvoice> findByOrganisation_OrgIDAndVendorIdOrderByInvoiceDateDesc(Long orgId, Long vendorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM VendorInvoice i WHERE i.id = :id AND i.organisation.orgID = :orgId")
    Optional<VendorInvoice> findByIdAndOrgForUpdate(@Param("id") Long id,
                                                    @Param("orgId") Long orgId);
}