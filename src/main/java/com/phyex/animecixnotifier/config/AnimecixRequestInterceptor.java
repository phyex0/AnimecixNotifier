package com.phyex.animecixnotifier.config;

import com.phyex.animecixnotifier.dto.SessionInfo;
import com.phyex.animecixnotifier.service.AnimecixSessionService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnimecixRequestInterceptor implements RequestInterceptor {

    private final AnimecixSessionService animecixSessionService;

    @Override
    public void apply(RequestTemplate template) {
        List<String> email = template.headers().get("email").stream().toList();
        SessionInfo sessionInfo = animecixSessionService.getSessionInfo(email.getFirst());


        String cookieHeader = sessionInfo.cookie().stream()
                .map(cookie -> cookie.substring(0, cookie.indexOf(';')))
                .collect(Collectors.joining("; "));
        template.header("Cookie", cookieHeader);


        String xsrfToken = sessionInfo.cookie().stream()
                .filter(cookie -> cookie.startsWith("XSRF-TOKEN="))
                .map(cookie -> cookie.substring("XSRF-TOKEN=".length()))
                .map(cookie -> cookie.split(";", 2)[0])
                .findFirst()
                .orElseThrow();
        template.header("X-XSRF-TOKEN", xsrfToken);
    }
}
