package com.phyex.animecixnotifier.repository;

import com.phyex.animecixnotifier.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserDocument, String> {

    Boolean existsByEmail(String email);
}
