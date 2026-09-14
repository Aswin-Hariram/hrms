package com.demo.HRMS.Services.Accounts;

import java.math.BigDecimal;

public interface Payment<P, R> {

    R pay(
            P payable,
            BigDecimal amount,
            Long paidBy,
            String paymentMode,
            String paymentReference
    );
}