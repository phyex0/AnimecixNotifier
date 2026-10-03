package com.phyex.animecixnotifier.service.telegram;

import com.phyex.animecixnotifier.config.SystemConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot implements SpringLongPollingBot {

    private final SystemConfig systemConfig;
    private final TelegramCommandDispatcher telegramCommandDispatcher;

    @Override
    public String getBotToken() {
        return systemConfig.getTelegramApiKey();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this::handle;
    }


    private void handle(List<Update> updates) {
        updates.forEach(update -> {
            log.debug("Update: {}, {}", update.getMessage().getChat(), update.getMessage().getText());
            telegramCommandDispatcher.dispatch(update);
        });
    }
}
