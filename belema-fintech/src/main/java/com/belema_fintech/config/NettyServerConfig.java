package com.belema_fintech.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "netty.server")
public class NettyServerConfig {
    private int port = 8583;
    private int bossThreads = 1;
    private int workerThreads = 4;
}