package Code.assetstore.controllers;

import Code.assetstore.models.*;
import Code.assetstore.repository.CategoryRepository;
import Code.assetstore.repository.UserRepository;
import Code.assetstore.security.services.AssetService;
import Code.assetstore.security.services.UserDetailsImpl;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/assets")
public class AssetsController {
    @Autowired
    private AssetService assetService;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add")
/*    public Assets add(@RequestBody Assets assets)*/
    public Assets add(@RequestParam("name") String name, @RequestParam("cost") long cost,
                      @RequestParam("description") String description,
                      @RequestParam("category") Set<String> category,
                      @RequestParam("file") MultipartFile file,
                      @RequestParam("img") MultipartFile img) throws IOException {
        /*Set<String> StrCategory = assets.getCategory();*/
        System.out.print(description);
        Set<String> StrCategory = category;
        Set<Category> categories = new HashSet<>();

        if (StrCategory == null) {
            Category assetCategory = categoryRepository.findByEname(ECategory.CATEGORY_OTHER)
                    .orElseThrow(() -> new RuntimeException("Error: Category is not found."));
            categories.add(assetCategory);
        } else {
            StrCategory.forEach(categoryy -> {
                switch (categoryy) {
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
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Set<User> user = userRepository.findByEmail(userDetails.getEmail());
        Assets assets = new Assets();
        assets.setName(name);
        assets.setCost(cost);
        assets.setUser(user);
        assets.setDescription(description);
        assets.setCategories(categories);
        return assetService.add(assets, file, img);
    }
    @PatchMapping("/update/{assetId}")
    public Assets update(@PathVariable(name = "assetId") String assetId,
                         @RequestParam("name") String name,
                         @RequestParam("description") String description,
                         @RequestParam(name = "cost", defaultValue = "0") int cost,
                         @RequestParam("category") Set<String> category,
                         @RequestParam("file") MultipartFile file,
                         @RequestParam("img") MultipartFile img) throws IOException{
        Assets assets = new Assets();
        assets.setName(name);
        assets.setDescription(description);
        assets.setCost(cost);
        assets.setCategory(category);
        return assetService.update(assets,assetId,file,img );
    }
    @GetMapping("/all")
    public List<Assets> getAllAssets(){
        return  assetService.getAllAssets();
    }
    @GetMapping("/by/{assetId}")
    public Assets getAsset(@PathVariable(name = "assetId") String assetId) {
        return assetService.getAsset(assetId);
    }
    @DeleteMapping("/delete/{assetId}")
    public void deleteAsset(@PathVariable(name = "assetId") String assetId) {
        Assets asset = assetService.getAsset(assetId);
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(asset.getFiles())));
        assetService.deleteAsset(assetId);
    }

    @GetMapping("/download/{fileId}")
    @ResponseBody
    public ResponseEntity<byte[]> getFile(@PathVariable(name = "fileId") String fileId, HttpServletResponse response) throws IOException{
        if (fileId == null) {
            return null;
        }
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId)));
        if (file == null) {
            return null;
        }
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(file.getObjectId());
        // Create gridFsResource, for obtaining the stream object
        GridFsResource gridFsResource = new GridFsResource(file, gridFSDownloadStream);
        byte[] byteFile = IOUtils.toByteArray(gridFsResource.getInputStream());
        ResponseEntity<byte[]> result = null;
        try {
            response.setStatus(HttpStatus.OK.value());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(byteFile.length);
            response.setHeader("Content-Disposition", "attachment; filename="+file.getFilename() + "."+ file.getMetadata().get("type"));
            result = new ResponseEntity<byte[]>(byteFile, headers, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }

    @GetMapping("/allcategories")
    public List<Category> getAllCategory(){
        return categoryRepository.findAll();
    }
}
