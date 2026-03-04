package com.belema_fintech.config;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class IsoMessageFactoryConfig {

    @Bean
    public MessageFactory<IsoMessage> messageFactory() throws IOException {
        MessageFactory<IsoMessage> factory = new MessageFactory<>();
        factory.setUseBinaryMessages(false);
        factory.setCharacterEncoding("UTF-8");
        factory.setConfigPath("iso8583.xml");

        //we need to be sure the bean is created at startup
        log.info("Message factory bean created");
        return factory;
    }
}