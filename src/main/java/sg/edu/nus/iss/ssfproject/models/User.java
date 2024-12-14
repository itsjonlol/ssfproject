package sg.edu.nus.iss.ssfproject.models;

import java.util.List;
import java.util.UUID;

public class User {
    
    private String id;
    private String username;
    private String email;
    private String password;
    private List<Anime> watchListAnime;
    private List<Anime> recommendedAnime;

    public User() {
        this.id = UUID.randomUUID().toString().substring(0,4);
    }

    

    public User(String id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }



    public User(String id, String username, String email, String password, List<Anime> watchListAnime,
            List<Anime> recommendedAnime) {
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
