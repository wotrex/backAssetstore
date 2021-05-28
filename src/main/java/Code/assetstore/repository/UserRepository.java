package Code.assetstore.repository;

import java.util.Optional;
import java.util.Set;

import Code.assetstore.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    Set<User> findByEmail(String email);

    Optional<User> findById(String id);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}