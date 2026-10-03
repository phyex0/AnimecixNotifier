package com.phyex.animecixnotifier.service.telegram.command;

import com.phyex.animecixnotifier.service.AnimecixUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateCommand implements TelegramCommand {

    private final AnimecixUserService animecixUserService;

    @Override
    public String command() {
        return "/update";
    }

    @Override
    public void executeCommand(Update update) {

    }
}
