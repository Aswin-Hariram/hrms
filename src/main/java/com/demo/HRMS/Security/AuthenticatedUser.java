package com.demo.HRMS.Security;


public record AuthenticatedUser(
        Long employeeId,
        Long organisationId
) {
}