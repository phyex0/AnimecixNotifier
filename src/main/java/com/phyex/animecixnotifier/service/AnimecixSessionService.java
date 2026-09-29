package com.phyex.animecixnotifier.service;

import com.phyex.animecixnotifier.dto.SessionInfo;

public interface AnimecixSessionService {

    SessionInfo getSessionInfo(String email);
}
