package com.belema_fintech.client;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

/**
 * Manual test client — run this while the server is running
 * to simulate real ISO 8583 traffic.
 */
@Slf4j
public class IsoTestClient {

    private static final String HOST = "localhost";
    private static final int PORT = 8583;

    public static void main(String[] args) throws Exception {
        MessageFactory<IsoMessage> factory = buildFactory();

        System.out.println("ISO 8583 Test Client");

        // Test 1: Approve N50.00
        System.out.println("TEST 1: Amount N50.00 → Expected: APPROVE (00)");
        byte [] testMessage1 = buildMessage(factory, "000000005000", "000001", "REF000000001");
        sendAndReceive(testMessage1);

        Thread.sleep(300);

        // Test 2: Decline N150.00
        System.out.println("\nTEST 2: Amount N150.00 → Expected: DECLINE (05)");
        byte [] testMessage2 = buildMessage(factory, "000000015000", "000002", "REF000000002");
        sendAndReceive(testMessage2);

        Thread.sleep(300);

        // Test 3: Boundary exactly N100.00
        System.out.println("\nTEST 3: Amount N100.00 → Expected: APPROVE (00)");
        byte [] testMessage3 = buildMessage(factory, "000000010000", "000003", "REF000000003");
        sendAndReceive(testMessage3);

        Thread.sleep(300);

        // Test 4: Just over boundary — N100.01
        System.out.println("\nTEST 4: Amount N100.01 → Expected: DECLINE (05)");
        byte [] testMessage4 = buildMessage(factory, "000000010001", "000004", "REF000000004");
        sendAndReceive(testMessage4);
    }

    private static void sendAndReceive(byte[] message) {
        try (Socket socket = new Socket(HOST, PORT)) {
            OutputStream out = socket.getOutputStream();
            InputStream  in  = socket.getInputStream();

            // Send: 2-byte length + message
            out.write((message.length >> 8) & 0xFF);
            out.write(message.length & 0xFF);
            out.write(message);
            out.flush();

            System.out.println("  → Sent     : " + new String(message));

            // Receive: 2-byte length + response
            int hi  = in.read();
            int lo  = in.read();
            int len = (hi << 8) | lo;

            byte[] responseBytes = new byte[len];
            int read = 0;
            while (read < len) {
                int n = in.read(responseBytes, read, len - read);
                if (n == -1) break;
                read += n;
            }

            String response = new String(responseBytes);
            System.out.println("  ← Received : " + response);
            System.out.println("  MTI        : " + response.substring(0, 4));

        } catch (Exception e) {
            System.err.println("  ERROR: " + e.getMessage());
        }
    }

    private static byte[] buildMessage(MessageFactory<IsoMessage> factory,
                                       String amount, String stan,
                                       String rrn) throws Exception {
        IsoMessage msg = factory.newMessage(0x100);
        msg.setValue(2,  "4111111111111111", IsoType.LLVAR,   0);
        msg.setValue(3,  "000000", IsoType.NUMERIC, 6);
        msg.setValue(4,  amount, IsoType.NUMERIC, 12);
        msg.setValue(7,  "0228143000", IsoType.NUMERIC, 10);
        msg.setValue(11, stan, IsoType.NUMERIC, 6);
        msg.setValue(12, "143000", IsoType.NUMERIC, 6);
        msg.setValue(13, "0228", IsoType.NUMERIC, 4);
        msg.setValue(14, "2612", IsoType.NUMERIC, 4);
        msg.setValue(18, "5411", IsoType.NUMERIC, 4);
        msg.setValue(22, "051", IsoType.NUMERIC, 3);
        msg.setValue(25, "00", IsoType.NUMERIC, 2);
        msg.setValue(28, "00000000", IsoType.NUMERIC, 8);
        msg.setValue(32, "000001", IsoType.LLVAR,   0);
        msg.setValue(37, rrn, IsoType.ALPHA,   12);
        msg.setValue(41, "TERM0001", IsoType.ALPHA,   8);
        msg.setValue(42, "MERCHANT0000001", IsoType.ALPHA,   15);
        msg.setValue(43, "TEST MERCHANT LAGOS NG          ", IsoType.ALPHA,   40);
        msg.setValue(49, "566", IsoType.NUMERIC, 3);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        msg.write(baos, 0);
        return baos.toByteArray();
    }

    private static MessageFactory<IsoMessage> buildFactory() throws IOException {
        MessageFactory<IsoMessage> factory = new MessageFactory<>();
        factory.setUseBinaryMessages(false);
        factory.setCharacterEncoding("UTF-8");
        factory.setConfigPath("iso8583.xml");
        return factory;
    }
}