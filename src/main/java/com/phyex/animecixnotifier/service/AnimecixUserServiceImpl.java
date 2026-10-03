package com.phyex.animecixnotifier.service;

import com.phyex.animecixnotifier.document.AnimeDocument;
import com.phyex.animecixnotifier.document.UserDocument;
import com.phyex.animecixnotifier.dto.LoginDTO;
import com.phyex.animecixnotifier.dto.NotifyDTO;
import com.phyex.animecixnotifier.dto.RegisterDTO;
import com.phyex.animecixnotifier.dto.lastepisodes.LastEpisodeData;
import com.phyex.animecixnotifier.dto.user.AnimeDTO;
import com.phyex.animecixnotifier.dto.user.UserDTO;
import com.phyex.animecixnotifier.repository.UserRepository;
import com.phyex.animecixnotifier.service.notify.AnimecixNotifyDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AnimecixUserServiceImpl implements AnimecixUserService {

    private final UserRepository userRepository;
    private final AnimecixFetchService animecixFetchService;
    private final AnimecixNotifyDispatcher animecixNotifyDispatcher;

    private final Map<String, LastEpisodeData> LAST_EPISODE_MAP = new ConcurrentHashMap<>();


    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {

        Boolean isUserExist = userRepository.existsByEmail(registerDTO.email());
        log.debug("Register request for : {} and user exists : {}", registerDTO, isUserExist);

        if (!isUserExist) {
            fetchUserDocument(new LoginDTO(registerDTO.email(), registerDTO.password(), false))
                    .ifPresent(userDocument -> {
                        userDocument.setNotifyType(registerDTO.notifyType());
                        userDocument.setNotifyId(registerDTO.notifyId());
                        userRepository.save(userDocument);
                    });
        }
    }

    @Override
    public Optional<UserDocument> fetchUserDocument(LoginDTO loginDTO) {

        Optional<UserDTO> fetchUser = animecixFetchService.fetchUser(loginDTO);
        log.debug("Fetch user response: {}", fetchUser);

        if (fetchUser.isPresent()) {
            UserDocument userDocument = new UserDocument();

            userDocument.setEmail(loginDTO.email());
            userDocument.setPassword(loginDTO.password());
            userDocument.setId(fetchUser.get().getUser().getId());
            userDocument.setAnimeDocumentList(mapWatchList(fetchUser.get().getWatchlist().getItems()));

            return Optional.of(userDocument);
        }
        return Optional.empty();
    }

    private List<AnimeDocument> mapWatchList(List<AnimeDTO> watchList) {
        return watchList.stream().map(item -> {
            AnimeDocument animeDocument = new AnimeDocument();

            animeDocument.setId(item.getId());
            animeDocument.setName(item.getName());
            animeDocument.setSeason(item.getSeasons());

            return animeDocument;
        }).toList();
    }

    @Override
    public void fetchLastEpisodes() {
        LAST_EPISODE_MAP.clear();

        animecixFetchService.fetchLastEpisodes(1).ifPresent(lastEpisode -> {
            log.debug("Fetch last episodes for page: 1");
            processLastEpisodes(lastEpisode.getData());

            for (int i = lastEpisode.getCurrentPage() + 1; i <= lastEpisode.getLastPage(); i++) {
                log.debug("Fetch last episodes for page: {}", i);
                animecixFetchService.fetchLastEpisodes(i)
                        .ifPresent(iterative -> processLastEpisodes(iterative.getData()));
            }
        });
    }

    private void processLastEpisodes(List<LastEpisodeData> lastEpisodeDataList) {
        log.debug("Last episode data list: {}", lastEpisodeDataList);
        lastEpisodeDataList.forEach(data -> LAST_EPISODE_MAP
                .merge(data.getTitleId(), data,
                        (existing, incoming) -> incoming.getReleaseDate().isAfter(existing.getReleaseDate()) ? incoming : existing));
    }

    @Override
    @Transactional
    public void updateUser(UserDocument userDocument) {

        fetchUserDocument(new LoginDTO(userDocument.getEmail(), userDocument.getPassword(), false))
                .ifPresent(remoteUser -> {
                    updateAnimeList(userDocument, remoteUser);
                    updateEpisode(userDocument);
                    userRepository.save(userDocument);
                });
    }

    private void updateAnimeList(UserDocument user, UserDocument remoteUser) {

        List<AnimeDocument> userList = user.getAnimeDocumentList();
        log.debug("UserList: {}", userList);

        List<AnimeDocument> remoteList = remoteUser.getAnimeDocumentList();
        log.debug("RemoteUserList: {}", remoteList);

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
            if (!remoteMap.containsKey(anime.getId())) return true;

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
                log.debug("Last episode data: {}", lastEpisodeData);

                if (Objects.isNull(anime.getReleaseDate()) ||
                        anime.getReleaseDate().isBefore(lastEpisodeData.getReleaseDate())) {
                    anime.setReleaseDate(lastEpisodeData.getReleaseDate());
                    anime.setSeason(lastEpisodeData.getSeason());
                    anime.setEpisode(lastEpisodeData.getEpisode());

                    String message = String.format("Dear user %s! New Episode for: %s, Season: %s, Episode: %s, Released at :%s", userDocument.getEmail(), anime.getName(), anime.getSeason(), anime.getEpisode(), anime.getReleaseDate());

                    animecixNotifyDispatcher.dispatch(new NotifyDTO(userDocument.getNotifyType(), userDocument.getNotifyId(), message));
                    log.info(message);
                    //TODO: Throw notification;
                }
            }
        });
    }
}
