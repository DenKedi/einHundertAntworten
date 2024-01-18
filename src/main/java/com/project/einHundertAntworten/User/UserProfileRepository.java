package com.project.einHundertAntworten.User;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {

    UserProfile findByUserID(String UserID);
    Optional<UserProfile> findOptionalByUserID(String UserID);
    boolean existsByUserID(String id);
}
