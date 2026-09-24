package com.phyex.animecixnotifier.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("user-document")
public class UserDocument implements Serializable {

    @Id
    private String id;

    private String email;

    private String password;

    private String phone;

    private List<AnimeDocument> animeDocumentList = new ArrayList<>();
}
