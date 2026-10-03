package com.phyex.animecixnotifier.service;

import com.phyex.animecixnotifier.client.AnimecixClient;
import com.phyex.animecixnotifier.dto.LoginDTO;
import com.phyex.animecixnotifier.dto.lastepisodes.LastEpisode;
import com.phyex.animecixnotifier.dto.user.UserDTO;
import com.phyex.animecixnotifier.exception.FetchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableRetry(proxyTargetClass = true)
public class AnimecixFetchServiceImpl implements AnimecixFetchService {

    private final AnimecixClient animecixClient;

    @Override
    @Retryable(recover = "recoverUser")
    public Optional<UserDTO> fetchUser(LoginDTO loginDTO) {

        ResponseEntity<UserDTO> userDTOResponseEntity = animecixClient.loginAndFetchUser(loginDTO.email(), loginDTO);
        log.debug("Fetch User Response: {}", userDTOResponseEntity);
        if (userDTOResponseEntity.getStatusCode().is2xxSuccessful()) {
            return Optional.ofNullable(userDTOResponseEntity.getBody());
        }
        throw new FetchException(String.format("Fetch user failed for: %s", loginDTO.email()));
    }

    @Recover
    public Optional<UserDTO> recoverUser(FetchException fe, LoginDTO loginDTO) {
        //TODO notify Admin
        log.error(fe.getMessage());
        return Optional.empty();
    }


    @Override
    @Retryable(recover = "recoverLastEpisode")
    public Optional<LastEpisode> fetchLastEpisodes(Integer page) {
        ResponseEntity<LastEpisode> lastEpisodeResponseEntity = animecixClient.fetchLastEpisode(page);
        if (lastEpisodeResponseEntity.getStatusCode().is2xxSuccessful()) {
            if (lastEpisodeResponseEntity.hasBody())
                return Optional.ofNullable(lastEpisodeResponseEntity.getBody());
        }

        throw new FetchException(String.format("Fetching at page %s is failed", page));
    }

    @Recover
    @Retryable(retryFor = FetchException.class)
    public Optional<LastEpisode> recoverLastEpisode(FetchException fe, Integer page) {
        //TODO notify Admin 
        log.error(fe.getMessage());
        return Optional.empty();
    }
}
