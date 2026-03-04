package com.belema_fintech.util;

import java.math.BigDecimal;

public final class AmountUtil {

    private static final BigDecimal CENTS  = new BigDecimal("100");
    private static final BigDecimal APPROVAL_LIMIT  = new BigDecimal("100.00");

    private AmountUtil() {}


     // "000000005000" ---------> 50.00
    public static BigDecimal convertFromIsoAmountToBigDecimal(String rawAmount) {
        return new BigDecimal(rawAmount.trim()).divide(CENTS);
    }


    public static String convertFromBigDecimalToIsoAmount(BigDecimal amount) {
        // 50.00 becomes ---------> "000000005000"
        return String.format("%012d", amount.multiply(CENTS).longValue());
    }

    public static boolean isAmountApprovable(BigDecimal amount) {
        return amount.compareTo(APPROVAL_LIMIT) <= 0;
    }
}