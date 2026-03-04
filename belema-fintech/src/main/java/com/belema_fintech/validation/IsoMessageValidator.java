package com.belema_fintech.validation;

import com.belema_fintech.domain.AuthorizationRequest;
import com.belema_fintech.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class IsoMessageValidator {

    public void validate(AuthorizationRequest req) {
        log.debug("Validating STAN: {}", req.getStan());

        validateMandatoryField(req.getPan(), "PAN");
        validateMandatoryField(req.getProcessingCode(), "Processing Code_Field3");

        if (req.getAmount() == null) {
            throw new ValidationException("Amount Field 4 is mandatory");
        }
        if (req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount Field 4 must be greater than zero");
        }

        validateMandatoryField(req.getTransmissionDateTime(), "Transmission DateTime Field 7");
        validateMandatoryField(req.getStan(),                 "STAN");
        validateMandatoryField(req.getLocalTransactionTime(), "Local Transaction Time");
        validateMandatoryField(req.getLocalTransactionDate(), "Local Transaction Date");
        validateMandatoryField(req.getExpirationDate(),       "Expiration Date");
        validateMandatoryField(req.getMerchantType(),         "Merchant Type");
        validateMandatoryField(req.getPosEntryMode(),          "POS Entry Mode");
        validateMandatoryField(req.getPosConditionCode(),      "POS Condition Code");
        validateMandatoryField(req.getTransactionFee(),        "Transaction Fee");
        validateMandatoryField(req.getAcquiringInstitution(),  "Acquiring Institution");
        validateMandatoryField(req.getRrn(),                   "RRN");
        validateMandatoryField(req.getTerminalId(),            "Terminal ID");
        validateMandatoryField(req.getMerchantId(),            "Merchant ID");
        validateMandatoryField(req.getCardAcceptorName(),      "Card Acceptor Name");
        validateMandatoryField(req.getCurrencyCode(),          "Currency Code");

        log.debug("Validation STAN: {} successfully", req.getStan());
    }

    private void validateMandatoryField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " is mandatory but missing or blank");
        }
    }
}