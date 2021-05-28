package Code.assetstore.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
public class Category {
    @Id
    private String id;
    private ECategory ename;
    private String name;

    public Category() {

    }

    public Category(ECategory ename, String name) {
        this.name = name;
        this.ename = ename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ECategory getEName() {
        return ename;
    }

    public void setEName(ECategory ename) {
        this.ename = ename;
    }
}
