package Code.assetstore.payloads;

import java.util.List;

public class JwtResponse {
    private String Token;
    private String type = "Bearer ";
    private String id;
    private String username;
    private String email;
    private List<String> roles;

    public JwtResponse(String token, String id, String username, String email, List<String> roles) {
        this.Token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public String getToken() {
        return type + Token;
    }

    public void setToken(String token) {
        this.Token = token;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }
}
