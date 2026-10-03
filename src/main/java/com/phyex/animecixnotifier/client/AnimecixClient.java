package com.phyex.animecixnotifier.client;

import com.phyex.animecixnotifier.config.AnimecixRequestInterceptor;
import com.phyex.animecixnotifier.dto.LoginDTO;
import com.phyex.animecixnotifier.dto.lastepisodes.LastEpisode;
import com.phyex.animecixnotifier.dto.user.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "AnimecixClient", url = "${system-config.animecix-url}", configuration = AnimecixRequestInterceptor.class)
public interface AnimecixClient {

    @PostMapping(value = "/secure/auth/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserDTO> loginAndFetchUser(@RequestHeader("email") String email, @RequestBody LoginDTO loginDTO);

    @GetMapping(value = "secure/last-episodes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<LastEpisode> fetchLastEpisode(@RequestParam(defaultValue = "1") Integer page);
}
