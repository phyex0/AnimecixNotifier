package com.phyex.animecixnotifier.client;

import com.phyex.animecixnotifier.config.AnimecixRequestInterceptor;
import com.phyex.animecixnotifier.dto.LoginDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import tools.jackson.databind.JsonNode;

@FeignClient(name = "AnimecixClient", url = "${client.animecix}", configuration = AnimecixRequestInterceptor.class)
public interface AnimecixClient {

    @PostMapping(value = "/secure/auth/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<JsonNode> loginAndFetchList(@RequestHeader("email") String email, @RequestBody LoginDTO loginDTO);
}
