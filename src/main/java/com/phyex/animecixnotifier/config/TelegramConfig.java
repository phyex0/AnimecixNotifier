package com.phyex.animecixnotifier.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
@RequiredArgsConstructor
public class TelegramConfig {

    private final SystemConfig systemConfig;

    @Bean
    public TelegramClient getTelegramClient() {
        return new OkHttpTelegramClient(systemConfig.getTelegramApiKey());
    }
}
