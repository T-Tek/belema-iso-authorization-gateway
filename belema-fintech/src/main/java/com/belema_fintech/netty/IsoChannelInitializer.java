package com.belema_fintech.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IsoChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final IsoMessageHandler handler;

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline()
                .addLast("decoder", new IsoMessageDecoder())
                .addLast("encoder", new IsoMessageEncoder())
                .addLast("handler", handler);
    }
}