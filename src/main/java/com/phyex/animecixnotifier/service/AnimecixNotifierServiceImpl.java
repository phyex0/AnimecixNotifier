package com.phyex.animecixnotifier.service;

import com.phyex.animecixnotifier.client.AnimecixClient;
import com.phyex.animecixnotifier.document.AnimeDocument;
import com.phyex.animecixnotifier.document.UserDocument;
import com.phyex.animecixnotifier.dto.LoginDTO;
import com.phyex.animecixnotifier.dto.RegisterDTO;
import com.phyex.animecixnotifier.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnimecixNotifierServiceImpl implements AnimecixNotifierService {

    private final AnimecixClient animecixClient;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {
        Boolean isUserExist = userRepository.existsByEmail(registerDTO.email());

        if (!isUserExist) {
            createUser(registerDTO).ifPresent(userRepository::save);
        }
    }

    private Optional<UserDocument> createUser(RegisterDTO registerDTO) {
        ResponseEntity<JsonNode> loginResponse = animecixClient.loginAndFetchList(new LoginDTO(registerDTO.email(), registerDTO.password(), false));

        if (loginResponse.getStatusCode().is2xxSuccessful()) {
            UserDocument userDocument = new UserDocument();

            userDocument.setEmail(registerDTO.email());
            userDocument.setPassword(registerDTO.password());
            userDocument.setPhone(registerDTO.phone());
            userDocument.setId(loginResponse.getBody().get("user").get("id").stringValue());
            userDocument.setAnimeDocumentList(parseWatchList(loginResponse.getBody().get("watchlist").get("items")));

            return Optional.of(userDocument);
        }

        return Optional.empty();
    }


    private List<AnimeDocument> parseWatchList(JsonNode watchList) {
        return watchList.valueStream().map(item -> {
            AnimeDocument animeDocument = new AnimeDocument();

            animeDocument.setId(item.get("id").stringValue());
            animeDocument.setName(item.get("name").stringValue());
            animeDocument.setSeason(item.get("seasons").asArray().size());
            animeDocument.setEpisode(item.get("seasons")
                    .valueStream()
                    .filter(ep -> ep.get("number").asInt() == animeDocument.getSeason())
                    .map(ep -> ep.get("episode_count").asInt())
                    .findFirst().orElse(0)
            );
            animeDocument.setReleaseDate(parseDate(item.get("release_date").asString()));

            return animeDocument;
        }).toList();
    }

    private LocalDateTime parseDate(String date) {
        if (StringUtils.isBlank(date))
            return null;

        if (date.contains("T")) {
            return OffsetDateTime.parse(date).toLocalDateTime();
        }

        return LocalDate.parse(date).atStartOfDay();
    }

    @Override
    public JsonNode loginAndFetchList() {
        return null;
    }

    @Override
    public JsonNode fetchAnime() {
        return null;
    }
}
