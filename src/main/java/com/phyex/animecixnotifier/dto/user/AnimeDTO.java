package com.phyex.animecixnotifier.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimeDTO {

    private String id;

    private String name;

    @JsonProperty("season_count")
    private Integer seasons;


}
