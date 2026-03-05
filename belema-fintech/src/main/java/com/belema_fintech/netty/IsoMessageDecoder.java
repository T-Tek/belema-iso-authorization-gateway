package com.belema_fintech.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class IsoMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // Wait for at least 2 bytes (length header)
        if (in.readableBytes() < 2) return;

        in.markReaderIndex();
        // 2-byte big-endian length
        int messageLength = in.readUnsignedShort();

        // Wait until the full message body is available
        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
            return;
        }

        byte[] messageBytes = new byte[messageLength];
        in.readBytes(messageBytes);

        log.info("Decoded message length: {}", messageLength);
        out.add(messageBytes);
    }
}