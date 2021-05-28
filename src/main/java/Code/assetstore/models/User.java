package Code.assetstore.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "users")
public class User {
    @Id
    private String id;

    private String username;

    private String email;

    private String password;

    private List<String> items;

    private List<String> cart;

    private List<String> sessions;

    @DBRef
    private Set<Role> roles = new HashSet<>();

    public User(){}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getId(){
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }
    public String getPassword(){
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public Set<Role> getRoles(){
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<String>  getItems(){
        return items;
    }

    public void setItems(List<String>  items) {
        this.items = items;
    }

    public List<String>  getCart(){
        return cart;
    }

    public void setCart(List<String>  cart) {
        this.cart = cart;
    }

    public List<String>  getSessions(){
        return sessions;
    }

    public void setSessions(List<String>  sessions) {
        this.sessions = sessions;
    }

}
