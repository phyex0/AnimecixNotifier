package com.phyex.animecixnotifier.service.notify;

import com.phyex.animecixnotifier.dto.NotifyDTO;
import com.phyex.animecixnotifier.enums.NotifyType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnimecixTelegramNotifier implements AnimecixNotifier {

    private final TelegramClient telegramClient;

    @Override
    public NotifyType notifyType() {
        return NotifyType.TELEGRAM;
    }

    @Override
    @SneakyThrows
    public void notify(NotifyDTO notifyDTO) {
        SendMessage sendMessage = new SendMessage(notifyDTO.notifyId(), notifyDTO.content());
        log.debug("Send Telegram message: {}", sendMessage);
        telegramClient.execute(sendMessage);
    }
}
