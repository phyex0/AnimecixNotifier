package com.phyex.animecixnotifier.dto.lastepisodes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LastEpisodeData {

    @JsonProperty("title_id")
    private String titleId;

    @JsonProperty("season_number")
    private Integer season;

    @JsonProperty("episode_number")
    private Integer episode;

    @JsonProperty("release_date")
    private Instant releaseDate;
}

