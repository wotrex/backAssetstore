package Code.assetstore.repository;

import Code.assetstore.models.Category;
import Code.assetstore.models.ECategory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {
    Optional<Category> findByEname(ECategory ename);
}
