package com.belema_fintech.service;

import com.belema_fintech.domain.AuthorizationDecision;
import com.belema_fintech.domain.AuthorizationRequest;
import com.belema_fintech.util.AmountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    @Override
    public AuthorizationDecision authorize(AuthorizationRequest request) {
        log.info("Processing authorization");

        if (AmountUtil.isAmountApprovable(request.getAmount())) {
            log.info("APPROVED");
            return AuthorizationDecision.approve();
        } else {
            log.info("DECLINED");
            return AuthorizationDecision.decline();
        }
    }

    private String maskPan(String pan) {
        if (pan == null) {
            return "******";
        }

        int panLength = pan.length();

        if (panLength < 10) {
            return "******";
        }

        String firstSixDigits = pan.substring(0, 6);
        String lastFourDigits = pan.substring(panLength - 4);
        int numberOfMaskedCharacters = panLength - 10;
        StringBuilder maskedSectionBuilder = new StringBuilder();

        for (int i = 0; i < numberOfMaskedCharacters; i++) {
            maskedSectionBuilder.append("*");
        }

        String maskedSection = maskedSectionBuilder.toString();

        return firstSixDigits + maskedSection + lastFourDigits;
    }
}