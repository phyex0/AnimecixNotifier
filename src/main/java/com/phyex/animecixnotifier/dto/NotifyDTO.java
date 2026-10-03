package com.phyex.animecixnotifier.dto;

import com.phyex.animecixnotifier.enums.NotifyType;

public record NotifyDTO(NotifyType notifyType, String notifyId, String content) {
}
