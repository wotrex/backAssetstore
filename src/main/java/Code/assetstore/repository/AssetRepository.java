package Code.assetstore.repository;

import Code.assetstore.models.Assets;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends MongoRepository<Assets, String> {
}
