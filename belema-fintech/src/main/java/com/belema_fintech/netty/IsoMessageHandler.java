package com.belema_fintech.netty;

import com.belema_fintech.domain.AuthorizationDecision;
import com.belema_fintech.domain.AuthorizationRequest;
import com.belema_fintech.exception.ValidationException;
import com.belema_fintech.iso.IsoMessageParser;
import com.belema_fintech.iso.IsoResponseBuilder;
import com.belema_fintech.service.AuthorizationService;
import com.belema_fintech.validation.IsoMessageValidator;
import com.solab.iso8583.IsoMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class IsoMessageHandler extends SimpleChannelInboundHandler<byte[]> {

    private final IsoMessageParser parser;
    private final IsoMessageValidator validator;
    private final AuthorizationService authorizationService;
    private final IsoResponseBuilder responseBuilder;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] rawBytes) {
        String stan = null;
        String rrn  = null;

        try {
            // 1. Parse raw bytes → IsoMessage
            IsoMessage isoMessage = parser.parse(rawBytes);

            // 2. Map to authorization object
            AuthorizationRequest request = parser.toRequest(isoMessage);
            stan = request.getStan();
            rrn  = request.getRrn();

            log.info("Received 0100, STAN: {}, RRN: {}", stan, rrn);

            // 3. Validate mandatory fields
            validator.validate(request);

            // 4. Make authorization decision
            AuthorizationDecision decision = authorizationService.authorize(request);

            // 5. Build and send 0110 response
            IsoMessage response = responseBuilder.buildResponse(request, decision);
            ctx.writeAndFlush(response);

            log.info("Sent 0110 STAN: {} with Response Code: {}", stan, decision.getResponseCode());

        } catch (ValidationException e) {
            log.warn("Validation failed for STAN: {} Reason: {}", stan, e.getMessage());
            ctx.writeAndFlush(responseBuilder.buildErrorResponse(stan, rrn, "05"));

        } catch (Exception e) {
            log.error("Unexpected error processing message | STAN: {}", stan, e);
            ctx.writeAndFlush(responseBuilder.buildErrorResponse(stan, rrn, "96"));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("Client connected: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("Client disconnected: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Channel exception, closing connection", cause);
        ctx.close();
    }
}