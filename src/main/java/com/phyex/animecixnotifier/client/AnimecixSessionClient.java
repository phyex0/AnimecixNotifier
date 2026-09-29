package com.phyex.animecixnotifier.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "AnimecixSessionClient", url = "${client.animecix}")
public interface AnimecixSessionClient {

    @GetMapping("/secure/bootstrap-data")
    ResponseEntity<String> bootstrapData(@RequestParam("original_url") String originalUrl);
}
