package com.belema_fintech.netty;

import com.belema_fintech.config.NettyServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IsoTcpServer implements CommandLineRunner {

    private final NettyServerConfig config;
    private final IsoChannelInitializer channelInitializer;

    @Override
    public void run(String... args) throws Exception {
        NioEventLoopGroup bossGroup   = new NioEventLoopGroup(config.getBossThreads());
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(config.getWorkerThreads());

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.bind(config.getPort()).sync();
            //started app
            log.info(" ISO 8583 Gateway started on port {}", config.getPort());

            future.channel().closeFuture().sync();

        } finally {
            log.info("Shutting down ISO 8583 Gateway...");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}