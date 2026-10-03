package com.phyex.animecixnotifier.controller;

import com.phyex.animecixnotifier.dto.RegisterDTO;
import com.phyex.animecixnotifier.service.AnimecixUserService;
import com.phyex.animecixnotifier.service.AnimecixScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController("/animecix-notifier")
public class AnimecixNotifierController {

    private final AnimecixUserService animecixUserService;
    private final AnimecixScheduleService animecixScheduleService;

    @PostMapping
    public ResponseEntity<Void> registerNotifier(@RequestBody @Valid RegisterDTO registerDTO) {
        animecixUserService.register(registerDTO);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/new-episode-scheduler")
    public ResponseEntity<Void> newEpisodeScheduler() {
        animecixScheduleService.newEpisodeScheduler();

        return ResponseEntity.ok().build();
    }
}
