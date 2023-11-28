package com.project.einHundertAntworten.User;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {

    UserProfile findByUserID(String UserID);

    boolean existsByUserID(String id);
}
