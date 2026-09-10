package com.demo.HRMS.Types;

public enum EmployeeStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    TERMINATED;

    public boolean isActive() {
        return this == ACTIVE;
    }
}