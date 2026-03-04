package com.belema_fintech.service;

import com.belema_fintech.domain.AuthorizationDecision;
import com.belema_fintech.domain.AuthorizationRequest;

public interface AuthorizationService {
    AuthorizationDecision authorize(AuthorizationRequest request);
}