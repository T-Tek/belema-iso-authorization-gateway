package com.belema_fintech.service;

import com.belema_fintech.domain.AuthorizationDecision;
import com.belema_fintech.domain.AuthorizationRequest;
import com.belema_fintech.util.AmountUtil;
import com.belema_fintech.util.CardUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.belema_fintech.util.CardUtil.maskPan;

@Slf4j
@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    @Override
    public AuthorizationDecision authorize(AuthorizationRequest request) {
        log.info("Processing authorization");

        if (AmountUtil.isAmountApprovable(request.getAmount())) {
            log.info("Approved amount: {} for PAN: {}", request.getAmount(), CardUtil.maskPan(request.getPan()));
            return AuthorizationDecision.approve();
        } else {
            log.info("Declined amount: {} for PAN: {}", request.getAmount(), CardUtil.maskPan(request.getPan()));
            return AuthorizationDecision.decline();
        }
    }

}