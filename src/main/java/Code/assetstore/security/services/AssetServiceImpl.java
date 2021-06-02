package Code.assetstore.security.services;

import Code.assetstore.models.Assets;
import Code.assetstore.models.Category;
import Code.assetstore.models.ECategory;
import Code.assetstore.models.Types;
import Code.assetstore.repository.AssetRepository;
import Code.assetstore.repository.CategoryRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AssetServiceImpl implements AssetService {
    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations operations;

    @Override
    public Assets add(Assets asset, MultipartFile file, MultipartFile img) throws IOException {
        DBObject metaData = new BasicDBObject();
        metaData.put("type", file.getOriginalFilename().split("\\.")[1]);
        ObjectId id = gridFsTemplate.store(
                file.getInputStream(), asset.getName(), file.getContentType(), metaData);
        asset.setFiles(id.toString());
        DBObject metaDataImg = new BasicDBObject();
        metaDataImg.put("type", img.getOriginalFilename().split("\\.")[1]);
        ObjectId imgId = gridFsTemplate.store(
                img.getInputStream(), asset.getName(), img.getContentType(), metaDataImg);
        asset.setImg(imgId.toString());
        Types type = new Types(file.getOriginalFilename().split("\\.")[1], file.getContentType());
        asset.setType(type);
        return assetRepository.save(asset);
    }

    @Override
    public Assets update(Assets asset, String assetId, MultipartFile file, MultipartFile img ) throws IOException  {
        Assets assets = assetRepository.findById(assetId).orElse(null);
        if (assets == null){
            return assets;
        }
        if(!file.isEmpty()){
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(assets.getFiles())));
            DBObject metaData = new BasicDBObject();
            metaData.put("type", file.getOriginalFilename().split("\\.")[1]);
            String name = assets.getName();
            Types type = new Types(file.getOriginalFilename().split("\\.")[1], file.getContentType());
            if (!asset.getName().equals("")){
                name = asset.getName();
            }
            ObjectId id = gridFsTemplate.store(
                    file.getInputStream(), name, file.getContentType(), metaData);
            assets.setFiles(id.toString());
            assets.setType(type);
        }
        if(!img.isEmpty()){
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(assets.getImg())));
            DBObject metaData = new BasicDBObject();
            metaData.put("type", img.getOriginalFilename().split("\\.")[1]);
            String name = assets.getName();
            if (!asset.getName().equals("")){
                name = asset.getName();
            }
            ObjectId idimg = gridFsTemplate.store(
                    img.getInputStream(), name, img.getContentType(), metaData);
            assets.setImg(idimg.toString());
        }
        assets.setId(assetId);
        if(!asset.getCategory().isEmpty()){
            Set<String> StrCategory = asset.getCategory();
            Set<Category> categories = new HashSet<>();
            StrCategory.forEach(category -> {
                switch (category) {
                    case "scripts":
                        Category scriptsCategory = categoryRepository.findByEname(ECategory.CATEGORY_SCRIPTS)
                                .orElseThrow(() -> new RuntimeException("Category is not found."));
                        categories.add(scriptsCategory);

                        break;
                    case "models":
                        Category modelsCategory = categoryRepository.findByEname(ECategory.CATEGORY_MODELS)
                                .orElseThrow(() -> new RuntimeException("Category is not found."));
                        categories.add(modelsCategory);

                        break;
                    default:
                        Category otherCategory = categoryRepository.findByEname(ECategory.CATEGORY_OTHER)
                                .orElseThrow(() -> new RuntimeException("Category is not found."));
                        categories.add(otherCategory);
                }
            });
            assets.setCategories(categories);
        }
        if(asset.getCost() != 0){
            assets.setCost(asset.getCost());
        }
        if(!asset.getName().equals("")) {
            assets.setName(asset.getName());
        }
        assetRepository.deleteById(assetId);
        return assetRepository.save(assets);
    }

    @Override
    public List<Assets> getAllAssets() {
        return assetRepository.findAll();
    }

    @Override
    public void deleteAsset(String assetId) {
        assetRepository.deleteById(assetId);
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(assetId)));

    }

    @Override
    public Assets getAsset(String assetId) {
        Assets assets = assetRepository.findById(assetId).orElse(null);
        return assets;
    }

}
