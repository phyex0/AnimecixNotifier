package com.phyex.animecixnotifier.controller;

import com.phyex.animecixnotifier.dto.RegisterDTO;
import com.phyex.animecixnotifier.service.AnimecixNotifierService;
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

    private final AnimecixNotifierService animecixNotifierService;

    @PostMapping
    public ResponseEntity<Void> registerNotifier(@RequestBody @Valid RegisterDTO registerDTO) {
        animecixNotifierService.register(registerDTO);

        return ResponseEntity.ok().build();
    }
}
