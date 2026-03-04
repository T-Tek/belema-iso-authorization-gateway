package com.belema_fintech.testConfig;

import com.belema_fintech.config.NettyServerConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    // Override the TCP server with a no-op so tests don't hang
    @Bean
    @Primary
    public com.belema_fintech.netty.IsoTcpServer isoTcpServer(
            NettyServerConfig config,
            com.belema_fintech.netty.IsoChannelInitializer initializer) {

        return new com.belema_fintech.netty.IsoTcpServer(config, initializer) {
            @Override
            public void run(String... args) {
            }
        };
    }
}