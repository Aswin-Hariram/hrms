package com.demo.HRMS.DTO.Vendor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateVendorResoponse {

    private Long id;
    private String vendorName;
    private String email;
    private String phoneNumber;
    private String address;
    private String contactPerson;
    private String gstNumber;
    private boolean active;
    private String message;
}