package com.phyex.animecixnotifier.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("user-document")
public class UserDocument implements Serializable {

    @Id
    private String id;

    private String email;

    private String password;

    private String telegramUser;

    private List<AnimeDocument> animeDocumentList = new ArrayList<>();

    @Transient
    public Map<String, AnimeDocument> getAnimeDocumentMap() {
        return animeDocumentList.stream().collect(Collectors.toMap(AnimeDocument::getId, Function.identity()));
    }
}
