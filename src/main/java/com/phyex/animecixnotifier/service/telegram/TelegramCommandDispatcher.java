package com.phyex.animecixnotifier.service.telegram;

import com.phyex.animecixnotifier.service.telegram.command.TelegramCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TelegramCommandDispatcher {

    private final Map<String, TelegramCommand> commandMap;

    public TelegramCommandDispatcher(List<TelegramCommand> commandList) {
        this.commandMap = commandList
                .stream()
                .collect(
                        Collectors.toMap(TelegramCommand::command, Function.identity())
                );
    }

    public void dispatch(Update update) {
        String[] parts = update.getMessage().getText().trim().split("\\s+");
        log.debug("Received telegram message: {}", (Object) parts);
        TelegramCommand command = commandMap.get(parts[0]);

        if (command == null) {
            return;
        }
        command.executeCommand(update);
    }
}
