package com.belema_fintech.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class AuthorizationRequest {
    private String pan;
    private String processingCode;
    private BigDecimal amount;
    private String transmissionDateTime;
    private String stan;
    private String localTransactionTime;
    private String localTransactionDate;
    private String expirationDate;
    private String merchantType;
    private String posEntryMode;
    private String posConditionCode;
    private String transactionFee;
    private String acquiringInstitution;
    private String rrn;
    private String terminalId;
    private String merchantId;
    private String cardAcceptorName;
    private String currencyCode;
}