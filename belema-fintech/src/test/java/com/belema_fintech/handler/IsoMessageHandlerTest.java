package com.belema_fintech.handler;

import com.belema_fintech.iso.IsoMessageParser;
import com.belema_fintech.iso.IsoResponseBuilder;
import com.belema_fintech.netty.IsoMessageDecoder;
import com.belema_fintech.netty.IsoMessageEncoder;
import com.belema_fintech.netty.IsoMessageHandler;
import com.belema_fintech.service.AuthorizationService;
import com.belema_fintech.testConfig.TestConfig;
import com.belema_fintech.validation.IsoMessageValidator;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestConfig.class)
class IsoMessageHandlerTest {

    @Autowired private IsoMessageParser parser;
    @Autowired private IsoMessageValidator validator;
    @Autowired private AuthorizationService authorizationService;
    @Autowired private IsoResponseBuilder responseBuilder;
    @Autowired private MessageFactory<IsoMessage> messageFactory;

    private EmbeddedChannel channel;

    @BeforeEach
    void setUp() {
        IsoMessageHandler handler = new IsoMessageHandler(
                parser, validator, authorizationService, responseBuilder);

        // Fresh decoder/encoder per test — ByteToMessageDecoder is stateful
        channel = new EmbeddedChannel(
                new IsoMessageDecoder(),
                new IsoMessageEncoder(),
                handler
        );
    }

    @Test
    void shouldApprove_whenAmountIsUnder100() throws Exception {
        writeToChannel(buildRequest("000000005000"));
        String response = readResponse();

        assertTrue(response.startsWith("0110"), "Expected MTI 0110 but got: " + response.substring(0, 4));
        assertTrue(response.contains("00"), "Expected approval code 00");
    }

    @Test
    void shouldApprove_whenAmountIsExactly100() throws Exception {
        writeToChannel(buildRequest("000000010000"));
        String response = readResponse();

        assertTrue(response.startsWith("0110"));
        assertTrue(response.contains("00"), "Exactly 100 should be approved");
    }

    @Test
    void shouldDecline_whenAmountIsOver100() throws Exception {
        writeToChannel(buildRequest("000000015000"));
        String response = readResponse();

        assertTrue(response.startsWith("0110"));
        assertTrue(response.contains("05"), "Expected decline code 05");
    }

    @Test
    void shouldDecline_whenAmountIsJustOverBoundary() throws Exception {
        writeToChannel(buildRequest("000000010001"));
        String response = readResponse();

        assertTrue(response.startsWith("0110"));
        assertTrue(response.contains("05"), "$100.01 should be declined");
    }
    private void writeToChannel(byte[] message) {
        ByteBuf buf = Unpooled.buffer();
        // 2-byte length prefix
        buf.writeShort(message.length);
        buf.writeBytes(message);
        channel.writeInbound(buf);
    }

    private String readResponse() {
        channel.runPendingTasks();

        ByteBuf response = channel.readOutbound();
        assertNotNull(response, "No response written to channel");

        response.readShort();
        byte[] bytes = new byte[response.readableBytes()];
        response.readBytes(bytes);
        return new String(bytes);
    }

    private byte[] buildRequest(String amount) throws Exception {
        IsoMessage msg = messageFactory.newMessage(0x100);
        msg.setValue(2,  "4111111111111111", IsoType.LLVAR,0);
        msg.setValue(3,  "000000", IsoType.NUMERIC, 6);
        msg.setValue(4,  amount, IsoType.NUMERIC, 12);
        msg.setValue(7,  "0228143000", IsoType.NUMERIC, 10);
        msg.setValue(11, "000001", IsoType.NUMERIC, 6);
        msg.setValue(12, "143000", IsoType.NUMERIC, 6);
        msg.setValue(13, "0228", IsoType.NUMERIC, 4);
        msg.setValue(14, "2612", IsoType.NUMERIC, 4);
        msg.setValue(18, "5411", IsoType.NUMERIC, 4);
        msg.setValue(22, "051", IsoType.NUMERIC, 3);
        msg.setValue(25, "00", IsoType.NUMERIC, 2);
        msg.setValue(28, "00000000", IsoType.NUMERIC, 8);
        msg.setValue(32, "000001", IsoType.LLVAR,   0);
        msg.setValue(37, "REF000000001", IsoType.ALPHA,   12);
        msg.setValue(41, "TERM0001", IsoType.ALPHA,   8);
        msg.setValue(42, "MERCHANT0000001", IsoType.ALPHA,   15);
        msg.setValue(43, "TEST MERCHANT LAGOS NG          ",  IsoType.ALPHA,   40);
        msg.setValue(49, "566", IsoType.NUMERIC, 3);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        msg.write(baos, 0);
        return baos.toByteArray();
    }
}