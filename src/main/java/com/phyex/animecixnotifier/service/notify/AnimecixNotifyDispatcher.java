package com.phyex.animecixnotifier.service.notify;

import com.phyex.animecixnotifier.dto.NotifyDTO;
import com.phyex.animecixnotifier.enums.NotifyType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AnimecixNotifyDispatcher {

    private final Map<NotifyType, AnimecixNotifier> notifierMap;

    public AnimecixNotifyDispatcher(List<AnimecixNotifier> notifierList) {
        this.notifierMap = notifierList.stream().collect(Collectors.toMap(AnimecixNotifier::notifyType, Function.identity()));
    }

    public void dispatch(NotifyDTO notifyDTO) {

        AnimecixNotifier animecixNotifier = notifierMap.get(notifyDTO.notifyType());
        log.debug("Notify event recieved: {}", notifyDTO);

        if (Objects.isNull(animecixNotifier)) {
            log.error("Invalid notify dispatcher");
            return;
        }
        animecixNotifier.notify(notifyDTO);
    }

}
