package com.phyex.animecixnotifier.client;

import com.phyex.animecixnotifier.dto.LoginDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import tools.jackson.databind.JsonNode;

@FeignClient(name = "AnimecixClient", url = "${client.animecix")
public interface AnimecixClient {

    @PostMapping(value = "/secure/short-login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<JsonNode> loginAndFetchList(LoginDTO loginDTO);
}
