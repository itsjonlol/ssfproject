package sg.edu.nus.iss.ssfproject.models;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class User {
    
    private String id;

    @NotEmpty(message = "Username is required")
    @Size(min = 3, max = 16, message = "Username must be between 3 and 16 characters")
    private String username;

    @NotEmpty(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotEmpty(message = "Password is required")
    @Size(min = 5, message = "Password must be at least 6 characters long")
    private String password;

    private List<Anime> watchListAnime;
    private List<Anime> recommendedAnime;

    public User() {
        this.id = UUID.randomUUID().toString().substring(0,4);
    }




    



    public User(String id,
            @NotEmpty(message = "Username is required") @Size(min = 3, max = 16, message = "Username must be between 3 and 16 characters") String username,
            @NotEmpty(message = "Email is required") @Email(message = "Please provide a valid email address") String email,
            @NotEmpty(message = "Password is required") @Size(min = 5, message = "Password must be at least 6 characters long") String password) {
        this.id = UUID.randomUUID().toString().substring(0,4);
        this.username = username;
        this.email = email;
        this.password = password;
    }




    public User(String id,
            @NotEmpty(message = "Username is required") @Size(min = 3, max = 16, message = "Username must be between 3 and 16 characters") String username,
            @NotEmpty(message = "Email is required") @Email(message = "Please provide a valid email address") String email,
            @NotEmpty(message = "Password is required") @Size(min = 5, message = "Password must be at least 6 characters long") String password,
            List<Anime> watchListAnime, List<Anime> recommendedAnime) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.watchListAnime = watchListAnime;
        this.recommendedAnime = recommendedAnime;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Anime> getWatchListAnime() {
        return watchListAnime;
    }

    public void setWatchListAnime(List<Anime> watchListAnime) {
        this.watchListAnime = watchListAnime;
    }

    public List<Anime> getRecommendedAnime() {
        return recommendedAnime;
    }

    public void setRecommendedAnime(List<Anime> recommendedAnime) {
        this.recommendedAnime = recommendedAnime;
    }

    
}
