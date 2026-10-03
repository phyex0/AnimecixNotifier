package com.phyex.animecixnotifier.service.telegram.command;

import com.phyex.animecixnotifier.dto.NotifyDTO;
import com.phyex.animecixnotifier.dto.RegisterDTO;
import com.phyex.animecixnotifier.enums.NotifyType;
import com.phyex.animecixnotifier.service.AnimecixUserService;
import com.phyex.animecixnotifier.service.notify.AnimecixNotifyDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterCommand implements TelegramCommand {

    private final AnimecixUserService animecixUserService;
    private final AnimecixNotifyDispatcher animecixNotifyDispatcher;

    @Override
    public String command() {
        return "/register";
    }

    @Override
    public void executeCommand(Update update) {
        String[] parts = update.getMessage().getText().trim().split("\\s+");

        if (parts.length != 3) {
            log.debug("Invalid pattern notify user");
            return;
        }
        RegisterDTO registerDTO = new RegisterDTO(parts[1],
                parts[2],
                NotifyType.TELEGRAM,
                String.valueOf(update.getMessage().getChatId())
        );
        try {
            animecixUserService.register(registerDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
            animecixNotifyDispatcher
                    .dispatch(new NotifyDTO(
                                    registerDTO.notifyType(),
                                    registerDTO.notifyId(),
                                    e.getMessage()
                            )
                    );
        }

    }
}
