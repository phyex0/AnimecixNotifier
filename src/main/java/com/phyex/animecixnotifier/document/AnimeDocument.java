package com.phyex.animecixnotifier.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimeDocument {

    private String id;

    private String name;

    private Integer season;

    private Integer episode;

    private Instant releaseDate;
}
