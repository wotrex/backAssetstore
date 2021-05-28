package Code.assetstore.payloads;

import Code.assetstore.models.Assets;
import Code.assetstore.models.Role;

import java.util.List;
import java.util.Set;

public class EditUserRequest {
    private String username;

    private String email;

    private String password;

    private List<String> items;

    private List<String>  cart;

    private List<String> sessions;


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
