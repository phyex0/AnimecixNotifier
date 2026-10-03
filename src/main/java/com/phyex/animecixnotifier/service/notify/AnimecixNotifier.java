package com.phyex.animecixnotifier.service.notify;

import com.phyex.animecixnotifier.dto.NotifyDTO;
import com.phyex.animecixnotifier.enums.NotifyType;

public interface AnimecixNotifier {

    NotifyType notifyType();

    void notify(NotifyDTO notifyDTO);

}
