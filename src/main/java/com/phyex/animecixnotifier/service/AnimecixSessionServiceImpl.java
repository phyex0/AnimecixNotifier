package com.phyex.animecixnotifier.service;

import com.phyex.animecixnotifier.client.AnimecixSessionClient;
import com.phyex.animecixnotifier.config.SystemConfig;
import com.phyex.animecixnotifier.dto.SessionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnimecixSessionServiceImpl implements AnimecixSessionService {

    private final SystemConfig systemConfig;
    private final AnimecixSessionClient animecixSessionClient;

    @Override
    public SessionInfo getSessionInfo(String email) {
        ResponseEntity<String> bootstrapData = animecixSessionClient.bootstrapData(systemConfig.getAnimecixUrl());
        log.debug("Session Info fetch response: {}", bootstrapData);
        List<String> setCookieHeaders = Optional
                .ofNullable(
                        bootstrapData
                                .getHeaders()
                                .get("Set-Cookie")
                )
                .orElse(Collections.emptyList());
        return new SessionInfo(email, setCookieHeaders);
    }
}
