package com.phyex.animecixnotifier.config;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("system-config")
public class SystemConfig {

    @NotBlank
    private String animecixUrl;

    private Integer batchSize = 1000;

    private Integer scheduleFixedRate = 2;

    @NotBlank
    private String telegramApiKey;
}
