package com.belema_fintech.iso;

import com.belema_fintech.domain.AuthorizationRequest;
import com.belema_fintech.exception.IsoParsingException;
import com.belema_fintech.util.AmountUtil;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoValue;
import com.solab.iso8583.MessageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static com.belema_fintech.util.CardUtil.maskPan;

@Slf4j
@Component
@RequiredArgsConstructor
public class IsoMessageParser {

    private final MessageFactory<IsoMessage> messageFactory;

//   Parses raw bytes into a j8583 IsoMessage.
//   The 2-byte length prefix has already been stripped by the decoder.

    public IsoMessage parse(byte[] rawBytes) {
        log.info("Received raw bytes: {} ", rawBytes);
        try {
            return messageFactory.parseMessage(rawBytes, 0);
        } catch (Exception e) {
            log.error("Failed to parse raw ISO 8583 bytes", e);
            throw new IsoParsingException("Failed to parse ISO 8583 message");
        }
    }

    public AuthorizationRequest toRequest(IsoMessage msg) {
        log.info("Mapping IsoMessage to Authorization object");
        return AuthorizationRequest.builder()
                .pan(field(msg, 2))
                .processingCode(field(msg, 3))
                .amount(AmountUtil.convertFromIsoAmountToBigDecimal(field(msg, 4)))
                .transmissionDateTime(field(msg, 7))
                .stan(field(msg, 11))
                .localTransactionTime(field(msg, 12))
                .localTransactionDate(field(msg, 13))
                .expirationDate(field(msg, 14))
                .merchantType(field(msg, 18))
                .posEntryMode(field(msg, 22))
                .posConditionCode(field(msg, 25))
                .transactionFee(field(msg, 28))
                .acquiringInstitution(field(msg, 32))
                .rrn(field(msg, 37))
                .terminalId(field(msg, 41))
                .merchantId(field(msg, 42))
                .cardAcceptorName(field(msg, 43))
                .currencyCode(field(msg, 49))
                .build();
    }

    private String field(IsoMessage msg, int fieldNum) {
        IsoValue<?> f = msg.getField(fieldNum);
        return f != null ? f.toString().trim() : null;
    }
}
