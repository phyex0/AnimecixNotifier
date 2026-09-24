package com.phyex.animecixnotifier.service;

import com.phyex.animecixnotifier.dto.RegisterDTO;
import tools.jackson.databind.JsonNode;

public interface AnimecixNotifierService {

    void register(RegisterDTO registerDTO);

    JsonNode loginAndFetchList();

    JsonNode fetchAnime();
}
