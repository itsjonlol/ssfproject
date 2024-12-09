package sg.edu.nus.iss.ssfproject.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.edu.nus.iss.ssfproject.constant.ConstantVar;
import sg.edu.nus.iss.ssfproject.constant.Url;
import sg.edu.nus.iss.ssfproject.models.Anime;
import sg.edu.nus.iss.ssfproject.repo.AnimeRepo;
import sg.edu.nus.iss.ssfproject.util.AnimeUtil;

@Service
public class AnimeService {
    
    @Autowired
    AnimeRepo animeRepo;

    Map<String,Integer> animeGenreMap = new HashMap();

    RestTemplate restTemplate = new RestTemplate();

    public void getAnimeGenre() {
        
        //get list of anime categories i want to show in my display page
        List<String> animeGenres = this.getAnimeGenres();
        
        ResponseEntity<String> data = restTemplate.getForEntity(Url.animeGenres, String.class); //1 is to 1 auto mapping
        String payload = data.getBody();
        
        InputStream is = new ByteArrayInputStream(payload.getBytes());
        JsonReader reader = Json.createReader(is);
        JsonObject jsonObject = reader.readObject();
        JsonArray jArray = jsonObject.getJsonArray("data");


        for (int i = 0; i <jArray.size();i++) {
            JsonObject jObject = jArray.getJsonObject(i);
            String genreName = jObject.getString("name");
            if (animeGenres.contains(genreName) ) {
                animeGenreMap.put(genreName,jObject.getInt("mal_id"));
                animeRepo.setHash(ConstantVar.genresRedisKey, genreName, String.valueOf(jObject.getInt("mal_id")));
            }
            
        }
        System.out.println(animeGenreMap.size());
        animeGenreMap.forEach((key, value) -> System.out.println(key + ":" + value));
        //map the anime category to its mal_id. so that i can call the api to get the top anime by its genre id
    }
    public Boolean hasRedisKey(String redisKey) {
        return animeRepo.hashExists(redisKey);

    }
    
    public List<String> getAnimeGenres() {
        return animeRepo.animeGenres();
    }

    public void getTopAnimeByGenre(String genre) {
        String getAnimeByGenreUrl = "";

    }

    public List<Anime> getAnimeListByGenre(String genre) {
       
        String genreId = String.valueOf(animeGenreMap.get(genre));
        String cacheKey = "anime: " + ConstantVar.animeListByGenreRedisKey;
        
        String animeByGenreIdUrl = String.format(Url.animesByGenreId,genreId);
        List<Anime> animeListByGenre = this.fetchAnimeApi(animeByGenreIdUrl);

    
        return animeListByGenre;
    }

    public List<Anime> getAnimeListByQuery(String query) {
        String animeByQueryUrl = String.format(Url.animesByQuery,query);
        List<Anime> animeListByQuery = this.fetchAnimeApi(animeByQueryUrl);
        return animeListByQuery;

    }

    public List<Anime> fetchAnimeApi(String url) {
        List<Anime> animeList = new ArrayList<>();

        ResponseEntity<String> data = restTemplate.getForEntity(url, String.class); //1 is to 1 auto mapping
        String payload = data.getBody();
        
        InputStream is = new ByteArrayInputStream(payload.getBytes());
        JsonReader reader = Json.createReader(is);
        JsonObject jsonObject = reader.readObject();
        JsonArray jArray = jsonObject.getJsonArray("data");

        for (int i = 0; i <jArray.size();i++) {

            JsonObject jObject = jArray.getJsonObject(i);

            Anime anime = AnimeUtil.parseJsonObject(jObject);
            
            animeList.add(anime);
        }
        return animeList;
    }
    public Anime getAnimeById(String id) {
        String animeByIdUrl = String.format(Url.animeById,id);
        ResponseEntity<String> data = restTemplate.getForEntity(animeByIdUrl, String.class); //1 is to 1 auto mapping
        String payload = data.getBody();
        
        InputStream is = new ByteArrayInputStream(payload.getBytes());
        JsonReader reader = Json.createReader(is);
        JsonObject jsonObject = reader.readObject();
        JsonObject jObject = jsonObject.getJsonObject("data");

        Anime anime = AnimeUtil.parseJsonObject(jObject);
        return anime;

    }

    
    
    
}
