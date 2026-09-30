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
import java.util.*;

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
            fetchUserDocument(new LoginDTO(registerDTO.email(), registerDTO.password(), true))
                    .ifPresent(userDocument -> {
                                userDocument.setPhone(registerDTO.phone());
                                userRepository.save(userDocument);
                            }
                    );
        }
    }

    @Override
    public Optional<UserDocument> fetchUserDocument(LoginDTO loginDTO) {
        ResponseEntity<JsonNode> loginResponse = animecixClient.loginAndFetchList(loginDTO.email(), loginDTO);

        if (loginResponse.getStatusCode().is2xxSuccessful()) {
            UserDocument userDocument = new UserDocument();

            userDocument.setEmail(loginDTO.email());
            userDocument.setPassword(loginDTO.password());
            userDocument.setId(loginResponse
                    .getBody()
                    .get("user")
                    .get("id")
                    .asString()
            );
            userDocument.setAnimeDocumentList(
                    parseWatchList(
                            loginResponse
                                    .getBody()
                                    .get("watchlist")
                                    .get("items")
                    )
            );

            return Optional.of(userDocument);
        }
        return Optional.empty();
    }

    private List<AnimeDocument> parseWatchList(JsonNode watchList) {
        return watchList
                .valueStream()
                .map(item -> {
                            AnimeDocument animeDocument = new AnimeDocument();

                            animeDocument.setId(item.get("id").asString());
                            animeDocument.setName(item.get("name").asString());
                            animeDocument.setSeason(item.get("season_count").asInt());

                            return animeDocument;
                        }
                )
                .toList();
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
    @Transactional
    public void updateAll() {
        userRepository.findAll().forEach(this::updateUser);
    }

    @Override
    public void updateUser(UserDocument userDocument) {
        fetchUserDocument(new LoginDTO(userDocument.getEmail(), userDocument.getPassword(), true))
                .ifPresent(remoteUser -> {
                            updateAnimeList(userDocument, remoteUser);
                            updateEpisode(userDocument);
                        }
                );
    }

    private void updateAnimeList(UserDocument user, UserDocument remoteUser) {
        List<AnimeDocument> userList = user.getAnimeDocumentList();
        List<AnimeDocument> remoteList = remoteUser.getAnimeDocumentList();

        if (userList == null) {
            userList = new ArrayList<>();
            user.setAnimeDocumentList(userList);
        }

        if (remoteList == null || remoteList.isEmpty()) {
            userList.clear();
            return;
        }

        Map<String, AnimeDocument> remoteMap = remoteUser.getAnimeDocumentMap();
        userList.removeIf(anime -> {
            if (!remoteMap.containsKey(anime.getId()))
                return true;

            AnimeDocument remoteAnime = remoteMap.get(anime.getId());
            anime.setSeason(remoteAnime.getSeason());

            return false;
        });

        Set<String> userAnimeIds = user.getAnimeDocumentMap().keySet();
        for (AnimeDocument remoteAnime : remoteList) {
            if (!userAnimeIds.contains(remoteAnime.getId())) {
                userList.add(remoteAnime);
            }
        }
    }

    @Override
    public void updateEpisode(UserDocument userDocument) {
        userDocument.getAnimeDocumentList().forEach(anime -> {
            ResponseEntity<JsonNode> episodeResponse = animecixClient.fetchEpisode(userDocument.getEmail(), anime.getId(), anime.getSeason());
            if (episodeResponse.getStatusCode().is2xxSuccessful()) {
                log.info(String.valueOf(episodeResponse.getBody()));
//                anime.setEpisode();
//                anime.setReleaseDate();
            }
        });
    }
}
