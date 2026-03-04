package com.belema_fintech.iso;

import com.belema_fintech.domain.AuthorizationDecision;
import com.belema_fintech.domain.AuthorizationRequest;
import com.belema_fintech.exception.IsoParsingException;
import com.belema_fintech.util.AmountUtil;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IsoResponseBuilder {

    private final MessageFactory<IsoMessage> messageFactory;

    public IsoMessage buildResponse(AuthorizationRequest request,
                                    AuthorizationDecision decision) {
        try {
            IsoMessage response = messageFactory.newMessage(0x110);

            //mandatory fields from request
            response.setValue(2,  request.getPan(), IsoType.LLVAR, 0);
            response.setValue(3,  request.getProcessingCode(), IsoType.NUMERIC, 6);
            response.setValue(4,  AmountUtil.convertFromBigDecimalToIsoAmount(request.getAmount()), IsoType.NUMERIC, 12);
            response.setValue(12, request.getLocalTransactionTime(), IsoType.NUMERIC, 6);
            response.setValue(18, request.getMerchantType(), IsoType.NUMERIC, 4);
            response.setValue(22, request.getPosEntryMode(), IsoType.NUMERIC, 3);
            response.setValue(25, request.getPosConditionCode(), IsoType.NUMERIC, 2);
            response.setValue(32, request.getAcquiringInstitution(), IsoType.LLVAR, 0);
            response.setValue(37, request.getRrn(), IsoType.ALPHA, 12);

            //authorization decision
            response.setValue(39, decision.getResponseCode(), IsoType.ALPHA, 2);
            response.setValue(49, request.getCurrencyCode(), IsoType.NUMERIC, 3);
            return response;

        } catch (Exception e) {
            log.error("Failed to build 0110 response", e);
            throw new IsoParsingException("Failed to build ISO 8583 response");
        }
    }

    /**
     * Builds a minimal error response when we don't have
     * a full request object (e.g. parse failure).
     */
    public IsoMessage buildErrorResponse(String stan, String rrn, String responseCode) {
        try {
            IsoMessage response = messageFactory.newMessage(0x110);
            if (stan != null) {
                response.setValue(11, stan, IsoType.NUMERIC, 6);
            }
            if (rrn != null) {
                response.setValue(37, rrn, IsoType.ALPHA, 12);
            }
            response.setValue(39, responseCode, IsoType.ALPHA, 2);
            return response;
        } catch (Exception e) {
            throw new IsoParsingException("Failed to build error response", e);
        }
    }
}