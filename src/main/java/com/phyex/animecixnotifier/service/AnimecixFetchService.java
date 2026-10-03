package com.phyex.animecixnotifier.service;

import com.phyex.animecixnotifier.dto.LoginDTO;
import com.phyex.animecixnotifier.dto.lastepisodes.LastEpisode;
import com.phyex.animecixnotifier.dto.user.UserDTO;

import java.util.Optional;

public interface AnimecixFetchService {

    Optional<UserDTO> fetchUser(LoginDTO loginDTO);

    Optional<LastEpisode> fetchLastEpisodes(Integer page);
}
