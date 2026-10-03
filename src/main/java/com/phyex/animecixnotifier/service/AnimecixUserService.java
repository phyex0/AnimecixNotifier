package com.phyex.animecixnotifier.service;

import com.phyex.animecixnotifier.document.UserDocument;
import com.phyex.animecixnotifier.dto.LoginDTO;
import com.phyex.animecixnotifier.dto.RegisterDTO;
import jakarta.validation.Valid;

import java.util.Optional;

public interface AnimecixUserService {

    void register(@Valid RegisterDTO registerDTO);

    Optional<UserDocument> fetchUserDocument(LoginDTO loginDTO);

    void fetchLastEpisodes();

    void updateUser(UserDocument userDocument);

    void updateEpisode(UserDocument userDocument);
}
