package Code.assetstore.repository;

import Code.assetstore.models.ERole;
import Code.assetstore.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}
