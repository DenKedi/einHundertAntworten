package com.project.einHundertAntworten;

import com.project.einHundertAntworten.User.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    User findByUsername(String username);

    User findByEmail(String email);
}
