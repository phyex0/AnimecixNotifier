package com.phyex.animecixnotifier.service;

import com.phyex.animecixnotifier.document.UserDocument;
import com.phyex.animecixnotifier.dto.LoginDTO;
import com.phyex.animecixnotifier.dto.RegisterDTO;

import java.util.Optional;

public interface AnimecixNotifierService {

    void register(RegisterDTO registerDTO);

    Optional<UserDocument> fetchUserDocument(LoginDTO loginDTO);

    void fetchLastEpisodes();

    void updateAll();

    void updateUser(UserDocument userDocument);

    void updateEpisode(UserDocument userDocument);
}
