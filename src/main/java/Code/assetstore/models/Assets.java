package Code.assetstore.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "assets")
public class Assets {
    @Id
    private String id;

    private String name;

    private long cost;
    @DBRef
    private Set<User> user;

    private String files;

    private Types filetype;

    @DBRef
    private Set<Category> categories = new HashSet<>();

    private Set<String> category;

    public Assets(){}

    public Assets(String name, int cost, Set<User> user) {
        this.name = name;
        this.cost = cost;
        this.user = user;
    }

    public String getId(){
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public long getCost() {
        return cost;
    }

    public void setCost(long cost){
        this.cost = cost;
    }
    public Set<User> getUser(){
        return user;
    }

    public void setType(Types filetype){
        this.filetype = filetype;
    }
    public Types getType(){
        return filetype;
    }

    public void setUser(Set<User> user) {
        this.user = user;
    }
    public Set<Category> getCategories(){
        return categories;
    }
    public Set<String> getCategory(){
        return category;
    }
    public void setCategory(Set<String> category) {
        this.category = category;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }
    public String getFiles(){
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }

}
