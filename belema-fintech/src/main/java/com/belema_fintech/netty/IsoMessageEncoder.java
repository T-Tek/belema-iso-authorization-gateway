package com.belema_fintech.netty;

import com.solab.iso8583.IsoMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class IsoMessageEncoder extends MessageToByteEncoder<IsoMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx,
                          IsoMessage msg, ByteBuf out) throws Exception {

        byte[] bytes = msg.writeData(); // no length prefix

        out.writeShort(bytes.length);   // 2-byte length prefix
        out.writeBytes(bytes);

        log.debug("Encoded response | length: {}", bytes.length);
    }
}