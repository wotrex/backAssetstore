package Code.assetstore.security.services;

import Code.assetstore.models.Assets;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
public interface AssetService {
    Assets add(Assets asset, MultipartFile file, MultipartFile img) throws IOException;
    Assets update(Assets asset, String assetId, MultipartFile file, MultipartFile img) throws IOException;
    List<Assets> getAllAssets();
    void deleteAsset(String assetId);
    Assets getAsset(String assetId);
}
