package com.phyex.animecixnotifier.service;

import com.phyex.animecixnotifier.client.AnimecixSessionClient;
import com.phyex.animecixnotifier.config.ClientConfig;
import com.phyex.animecixnotifier.dto.SessionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnimecixSessionServiceImpl implements AnimecixSessionService {

    private final ClientConfig clientConfig;
    private final AnimecixSessionClient animecixSessionClient;
    private final RedisTemplate<String, SessionInfo> redisTemplate;


    @Override
    public SessionInfo getSessionInfo(String email) {

        SessionInfo sessionInfo = (SessionInfo) redisTemplate.opsForHash().get(email, SessionInfo.class.getName());

        if (Objects.isNull(sessionInfo)) {
            ResponseEntity<String> bootstrapData = animecixSessionClient.bootstrapData(clientConfig.getAnimecix());
            List<String> setCookieHeaders = Optional.ofNullable(bootstrapData.getHeaders().get("Set-Cookie")).orElse(Collections.emptyList());


            sessionInfo = new SessionInfo(email, setCookieHeaders, null);
            redisTemplate.opsForHash().put(sessionInfo.email(), SessionInfo.class.getName(), sessionInfo);
        }
        return sessionInfo;
    }
}
