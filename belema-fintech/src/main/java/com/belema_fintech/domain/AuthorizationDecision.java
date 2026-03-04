package com.belema_fintech.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthorizationDecision {

    private String responseCode;
    private boolean approved;

    public static AuthorizationDecision approve() {
        return AuthorizationDecision.builder()
                .responseCode("00")
                .approved(true)
                .build();
    }

    public static AuthorizationDecision decline() {
        return AuthorizationDecision.builder()
                .responseCode("05")
                .approved(false)
                .build();
    }

    public static AuthorizationDecision error() {
        return AuthorizationDecision.builder()
                .responseCode("96")
                .approved(false)
                .build();
    }
}