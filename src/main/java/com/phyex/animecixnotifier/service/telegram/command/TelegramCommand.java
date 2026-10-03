package com.phyex.animecixnotifier.service.telegram.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramCommand {

    String command();

    void executeCommand(Update update);
}
