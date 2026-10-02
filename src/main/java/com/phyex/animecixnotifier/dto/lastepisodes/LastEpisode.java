package com.phyex.animecixnotifier.dto.lastepisodes;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LastEpisode {

    @JsonProperty("current_page")
    private Integer currentPage;

    @JsonProperty("last_page")
    private Integer lastPage;

    private List<LastEpisodeData> data = new ArrayList<>();
}
