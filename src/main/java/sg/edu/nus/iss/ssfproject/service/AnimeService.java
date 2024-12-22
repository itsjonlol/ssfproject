package sg.edu.nus.iss.ssfproject.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    @Autowired
    private ObjectMapper objectMapper;

    Map<String,Integer> animeGenreMap;

    RestTemplate restTemplate = new RestTemplate();
 

    public void getAnimeGenre() {

        //a map is required to map the anime genre name to its id.
        //not allowed to fetch anime of different categories via genre name. required to use genre id.
         //map the anime category to its mal_id. so that i can call the api to get the top anime by its genre id
        animeGenreMap = new HashMap<>();
        
        //fetch the different anime genres to cater to most audiences
        ResponseEntity<String> standardGenreData = restTemplate.getForEntity(Url.animeGenres+"?filter=genres", String.class);
        
        ResponseEntity<String> demographicGenreData = restTemplate.getForEntity(Url.animeGenres+"?filter=demographics", String.class);
        
        processGenreResponse(standardGenreData.getBody());
        processGenreResponse(demographicGenreData.getBody());
        
    }
    
    // Helper method to process the API response for the different genres
    private void processGenreResponse(String payload) {
        InputStream is = new ByteArrayInputStream(payload.getBytes());
        JsonReader reader = Json.createReader(is);
        JsonObject jsonObject = reader.readObject();
        JsonArray jArray = jsonObject.getJsonArray("data");
        
        for (int i = 0; i < jArray.size(); i++) {
            JsonObject jObject = jArray.getJsonObject(i);
            String genreName = jObject.getString("name");
            //additional genre filter, to cater to most audiences
            if (!genreName.toLowerCase().equals("boys love") && !genreName.toLowerCase().equals("girls love")) {
                animeGenreMap.put(genreName, jObject.getInt("mal_id"));
                //store the genre names with their respective id into redis
                animeRepo.setHash(ConstantVar.genresRedisKey, genreName, String.valueOf(jObject.getInt("mal_id")));
            }
            
        }
        animeGenreMap.forEach((key, value) -> System.out.println(key + ":" + value));
        System.out.println(animeGenreMap.size());
    }
    
    //get the list of genres to display in front page
    public List<String> getAnimeGenres() {
        return animeRepo.animeGenres();
    }

    
    //get the list of anime for the selected genre
    public List<Anime> getAnimeListByGenre(String genre) throws JsonProcessingException  {
        List<Anime> animeListByGenre;
        
        String genreId = String.valueOf(animeGenreMap.get(genre));
        String animeByGenreIdUrl = String.format(Url.animesByGenreId,genreId);

        //cache hit to reduce load times. check to see if the genre exists in redis.
        animeListByGenre = this.getCachedAnimesByGenre(genre);
        if (!animeListByGenre.isEmpty()) {
            System.out.println("cache hit");
            return animeListByGenre;
        }

        //cache miss-> a) do an api call based on the genre.
        animeListByGenre = this.fetchAnimeApi(animeByGenreIdUrl);
        
        //to account for api error
        if (!animeListByGenre.isEmpty()) {
            if ( animeListByGenre.getFirst().getTitle().equals("apierror") ) {
                return animeListByGenre;
            }
        }
        // b) store the list of anime for that genre into redis for caching purposes as a map
        Map<String, String> animeMap = new HashMap<>();
        
        for (Anime anime : animeListByGenre) {
            //automapping -> convert anime object into an json-formatted string
            String animeJsonString = objectMapper.writeValueAsString(anime);
            animeMap.put(String.valueOf(anime.getMal_id()), animeJsonString);
           
        }
        animeRepo.setMapAll(genre, animeMap);
        System.out.println("cache miss");
    
        return animeListByGenre;
    }
    
    //helper method to retrieve the cached list of anime for a particular genre
    private List<Anime> getCachedAnimesByGenre(String genre) throws JsonMappingException, JsonProcessingException {
        // Retrieve the list of anime data stored in Redis for the specified genre
        List<Object> objectList = animeRepo.getAllValuesFromHash(genre);
        List<Anime> animes = new ArrayList<>();
    
        // Loop through the objectList and convert each json string into an Anime object
        for (Object data : objectList) {
            String animeDataJsonString = (String) data; 
            Anime anime = objectMapper.readValue(animeDataJsonString, Anime.class);
            animes.add(anime);
        }
    
        return animes;
    }

    //function to return a list of anime based on what user searched in the front page.
    public List<Anime> getAnimeListByQuery(String query) {
        
        String animeByQueryUrl = String.format(Url.animesByQuery,query);
        List<Anime> animeListByQuery = this.fetchAnimeApi(animeByQueryUrl);
        //filter the type of anime able to be searched.
        Set<String> allowedAnimeTypes = Stream.of("TV", "MOVIE", "OVA", "ONA","TV SPECIAL")
        .collect(Collectors.toUnmodifiableSet());
        //for api errors
        if (!animeListByQuery.isEmpty()) {
            if ( animeListByQuery.getFirst().getTitle().equals("apierror") ) {
                return animeListByQuery;
            }
        }

        return animeListByQuery.stream()
    .filter(anime -> anime.getType() != null && allowedAnimeTypes.contains(anime.getType().toUpperCase()))
    .collect(Collectors.toList());
}

    
    //generic helper function to fetch an api based on a specific url. returns a list of anime from that url.
    public List<Anime> fetchAnimeApi(String url) {
        List<Anime> animeList = new ArrayList<>();
        try {
            ResponseEntity<String> data = restTemplate.getForEntity(url, String.class); 
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
            // in case of any api errors
        } catch (RestClientException ex) {
            System.out.println(ex.getMessage());
            Anime anime = new Anime();
            anime.setTitle("apierror");
            animeList.add(anime);
        }
        
        return animeList;
    }
    //function to retrieve anime details when a user clicks on an anime card profile.
    public Anime getAnimeById(String id) {
        String animeByIdUrl = String.format(Url.animeById,id);
        Anime anime = new Anime();
        try {
            ResponseEntity<String> data = restTemplate.getForEntity(animeByIdUrl, String.class);
            String payload = data.getBody();
            
            InputStream is = new ByteArrayInputStream(payload.getBytes());
            JsonReader reader = Json.createReader(is);
            JsonObject jsonObject = reader.readObject();
            JsonObject jObject = jsonObject.getJsonObject("data");

            anime = AnimeUtil.parseJsonObject(jObject);
            
        } catch (RestClientException ex) {
            System.out.println("anime id service error " + ex.getMessage());
            
            anime.setTitle("apierror");
        }
        return anime;
    }

    public Map<String, Integer> getAnimeGenreMap() {
        return animeGenreMap;
    }

}