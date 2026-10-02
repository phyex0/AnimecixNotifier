package com.phyex.animecixnotifier.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private User user;

    private Watchlist watchlist;

    @Data
    public static class User {
        private String id;
    }

    @Data
    public static class Watchlist {
        private List<AnimeDTO> items = new ArrayList<>();
    }
}
