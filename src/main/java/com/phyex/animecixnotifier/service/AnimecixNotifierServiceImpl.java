package com.phyex.animecixnotifier.service;

import com.phyex.animecixnotifier.client.AnimecixClient;
import com.phyex.animecixnotifier.document.AnimeDocument;
import com.phyex.animecixnotifier.document.UserDocument;
import com.phyex.animecixnotifier.dto.LoginDTO;
import com.phyex.animecixnotifier.dto.RegisterDTO;
import com.phyex.animecixnotifier.dto.lastepisodes.LastEpisode;
import com.phyex.animecixnotifier.dto.lastepisodes.LastEpisodeData;
import com.phyex.animecixnotifier.dto.user.AnimeDTO;
import com.phyex.animecixnotifier.dto.user.UserDTO;
import com.phyex.animecixnotifier.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnimecixNotifierServiceImpl implements AnimecixNotifierService {

    private final AnimecixClient animecixClient;
    private final UserRepository userRepository;

    private final Map<String, LastEpisodeData> LAST_EPISODE_MAP = new ConcurrentHashMap<>();


    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {
        Boolean isUserExist = userRepository.existsByEmail(registerDTO.email());

        if (!isUserExist) {
            fetchUserDocument(new LoginDTO(registerDTO.email(), registerDTO.password(), false))
                    .ifPresent(userDocument -> {
                                userDocument.setTelegramUser(registerDTO.telegramUser());
                                userRepository.save(userDocument);
                            }
                    );
        }
    }

    @Override
    public Optional<UserDocument> fetchUserDocument(LoginDTO loginDTO) {
        ResponseEntity<UserDTO> userDTOResponse = animecixClient.loginAndFetchUser(loginDTO.email(), loginDTO);

        if (userDTOResponse.getStatusCode().is2xxSuccessful()) {
            UserDTO userDTO = userDTOResponse.getBody();
            UserDocument userDocument = new UserDocument();

            userDocument.setEmail(loginDTO.email());
            userDocument.setPassword(loginDTO.password());
            userDocument.setId(userDTO.getUser().getId());
            userDocument.setAnimeDocumentList(mapWatchList(userDTO.getWatchlist().getItems()));

            return Optional.of(userDocument);
        }
        return Optional.empty();
    }

    private List<AnimeDocument> mapWatchList(List<AnimeDTO> watchList) {
        return watchList
                .stream()
                .map(item -> {
                            AnimeDocument animeDocument = new AnimeDocument();

                            animeDocument.setId(item.getId());
                            animeDocument.setName(item.getName());
                            animeDocument.setSeason(item.getSeasons());

                            return animeDocument;
                        }
                )
                .toList();
    }

    @Override
    public void fetchLastEpisodes() {
        LAST_EPISODE_MAP.clear();

        ResponseEntity<LastEpisode> lastEpisodeResponseEntity = animecixClient.fetchLastEpisode();

        if (lastEpisodeResponseEntity.getStatusCode().is2xxSuccessful()) {
            LastEpisode lastEpisode = lastEpisodeResponseEntity.getBody();
            processLastEpisodes(lastEpisode.getData());

            for (int i = lastEpisode.getCurrentPage() + 1; i <= lastEpisode.getLastPage(); i++) {
                ResponseEntity<LastEpisode> iterativeLastEpisode = animecixClient.fetchLastEpisode(i);
                if (iterativeLastEpisode.getStatusCode().is2xxSuccessful())
                    processLastEpisodes(iterativeLastEpisode.getBody().getData());

            }
        }

    }

    private void processLastEpisodes(List<LastEpisodeData> lastEpisodeDataList) {
        lastEpisodeDataList.forEach(data ->
                LAST_EPISODE_MAP.merge(
                        data.getTitleId(),
                        data,
                        (existing, incoming) ->
                                incoming.getReleaseDate().isAfter(existing.getReleaseDate())
                                        ? incoming
                                        : existing
                )
        );
    }

    @Override
    @Transactional
    public void updateAll() {
        fetchLastEpisodes();

        userRepository.findAll().forEach(this::updateUser);
    }

    @Override
    public void updateUser(UserDocument userDocument) {
        fetchUserDocument(
                new LoginDTO(userDocument.getEmail(), userDocument.getPassword(), false)
        )
                .ifPresent(remoteUser -> {
                            updateAnimeList(userDocument, remoteUser);
                            updateEpisode(userDocument);
                            userRepository.save(userDocument);
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
            if (LAST_EPISODE_MAP.containsKey(anime.getId())) {
                LastEpisodeData lastEpisodeData = LAST_EPISODE_MAP.get(anime.getId());

                if (Objects.isNull(anime.getReleaseDate()) || anime.getReleaseDate().isBefore(lastEpisodeData.getReleaseDate())) {
                    anime.setReleaseDate(lastEpisodeData.getReleaseDate());
                    anime.setSeason(lastEpisodeData.getSeason());
                    anime.setEpisode(lastEpisodeData.getEpisode());

                    log.info("Dear user {}! New Episode for: {}, Season: {}, Episode: {}, Released at :{}",
                            userDocument.getEmail(),
                            anime.getName(),
                            anime.getSeason(),
                            anime.getEpisode(),
                            anime.getReleaseDate()
                    );
                    //TODO: Throw notification;
                }
            }
        });
    }
}
