package sg.edu.nus.iss.ssfproject.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        animeGenreMap = new HashMap<>();
        //get list of anime categories i want to show in my display page
        // List<String> animeGenres = this.getAnimeGenres();
        
        ResponseEntity<String> standardGenreData = restTemplate.getForEntity(Url.animeGenres+"?filter=genres", String.class);
        
        ResponseEntity<String> demographicGenreData = restTemplate.getForEntity(Url.animeGenres+"?filter=demographics", String.class);
        
        processGenreResponse(standardGenreData.getBody());
        processGenreResponse(demographicGenreData.getBody());
        
        System.out.println(animeGenreMap.size());
        animeGenreMap.forEach((key, value) -> System.out.println(key + ":" + value));
        //map the anime category to its mal_id. so that i can call the api to get the top anime by its genre id
    }
    
    // Helper method to process the API response
    private void processGenreResponse(String payload) {
        InputStream is = new ByteArrayInputStream(payload.getBytes());
        JsonReader reader = Json.createReader(is);
        JsonObject jsonObject = reader.readObject();
        JsonArray jArray = jsonObject.getJsonArray("data");
        
        for (int i = 0; i < jArray.size(); i++) {
            JsonObject jObject = jArray.getJsonObject(i);
            String genreName = jObject.getString("name");
            if (!genreName.toLowerCase().equals("boys love") && !genreName.toLowerCase().equals("girls love")) {
                animeGenreMap.put(genreName, jObject.getInt("mal_id"));
                animeRepo.setHash(ConstantVar.genresRedisKey, genreName, String.valueOf(jObject.getInt("mal_id")));
            }
            
        }
    }
    
    public Boolean hasRedisKey(String redisKey) {
        return animeRepo.hashExists(redisKey);

    }
    
    public List<String> getAnimeGenres() {
        return animeRepo.animeGenres();
    }

    

    public List<Anime> getAnimeListByGenre(String genre) throws JsonProcessingException  {
        List<Anime> animeListByGenre = new ArrayList<>();
        // String genreId = String.valueOf(animeGenreMap.get(genre));
        String genreId = animeRepo.getValueFromHash(ConstantVar.genresRedisKey, genre);
        
        String animeByGenreIdUrl = String.format(Url.animesByGenreId,genreId);
        //cache hit to reduce load times
        if (animeRepo.hashExists(genre)) {
            animeListByGenre = this.getCachedAnimesByGenre(genre);
            return animeListByGenre;
        }
        //cache miss-> store inside redis
        animeListByGenre = this.fetchAnimeApi(animeByGenreIdUrl);
        
        //for api error
        if (!animeListByGenre.isEmpty()) {
            if ( animeListByGenre.getFirst().getTitle().equals("apierror") ) {
                return animeListByGenre;
            }
        }
        
        for (Anime anime : animeListByGenre) {
            this.saveAnimeByGenre(anime,genre);
        }
        
        return animeListByGenre;
    }

    // private Integer mal_id;
    // //which image
    // private String large_image_url;

    // // youtube trailer omit first
    // private String title;
    // private String title_japanese;
    // private String type;
    // private Integer episodes;
    // private String status;
    // private String duration;
    // private Double score;
    // private Integer rank;
    // private String synopsis;
    // private Integer year;
    // private List<String> producers;
    // private List<String> studios;
    // private List<String> genres;
    public void saveAnimeByGenre(Anime anime,String category) throws JsonProcessingException {

        String animeJsonString = objectMapper.writeValueAsString(anime);

       //Store the serialized anime in Redis under the category
        animeRepo.setHash(category, String.valueOf(anime.getMal_id()), animeJsonString);
        
//         JsonObjectBuilder builder = Json.createObjectBuilder();

// builder.add("mal_id", anime.getMal_id() != null ? anime.getMal_id() : null);
// builder.add("large_image_url", anime.getLarge_image_url() != null ? anime.getLarge_image_url() : null);
// builder.add("title", anime.getTitle() != null ? anime.getTitle() : null);
// JsonArrayBuilder genreArrayBuilder = Json.createArrayBuilder();


// List<String> genres = anime.getGenres();
// if (genres !=null && !genres.isEmpty()) {
//     for (String genre : genres) {
//         genreArrayBuilder.add(genre);
//     }
// }
// //build first
// JsonArray genreArray = genreArrayBuilder.build();
// Handle lists
// JsonArrayBuilder producerArrayBuilder = Json.createArrayBuilder();
// if (anime.getProducers() != null && !anime.getProducers().isEmpty()) {
//     for (String producer : anime.getProducers()) {
//         producerArrayBuilder.add(Json.createObjectBuilder().add("name", producer));
//     }
// } else {
//     producerArrayBuilder.add(JsonValue.NULL);
// }
// builder.add("producers", producerArrayBuilder.build());

// JsonArrayBuilder studioArrayBuilder = Json.createArrayBuilder();
// if (anime.getStudios() != null && !anime.getStudios().isEmpty()) {
//     for (String studio : anime.getStudios()) {
//         studioArrayBuilder.add(Json.createObjectBuilder().add("name", studio));
//     }
// } else {
//     studioArrayBuilder.add(JsonValue.NULL);
// }
// builder.add("studios", studioArrayBuilder.build());

// JsonArrayBuilder genreArrayBuilder = Json.createArrayBuilder();
// if (anime.getGenres() != null && !anime.getGenres().isEmpty()) {
//     for (String genre : anime.getGenres()) {
//         genreArrayBuilder.add(Json.createObjectBuilder().add("name", genre));
//     }
// } else {
//     genreArrayBuilder.add(JsonValue.NULL);
// }
// builder.add("genres", genreArrayBuilder.build());

// Finally build the JsonObject

////
// JsonObject animeJsonObject = builder
//                             .add("genres",genreArray)
//                             .build();
// animeRepo.setHash(category, String.valueOf(anime.getMal_id()), animeJsonObject.toString()); // doesnt matter put here or savelist func
///
        // JsonArrayBuilder producerBuilder = Json.createArrayBuilder();
        // JsonArrayBuilder studioBuilder = Json.createArrayBuilder();
        // JsonArrayBuilder genreBuilder = Json.createArrayBuilder();
        
        // for (String producer : anime.getProducers()) {
        //     JsonObject producerJsonObject = Json.createObjectBuilder()
        //                                         .add("name",producer)
        //                                         .build();
        //     producerBuilder.add(producerJsonObject);
        // }
        // for (String studio : anime.getStudios()) {
        //     JsonObject studioJsonObject = Json.createObjectBuilder()
        //                                         .add("name",studio)
        //                                         .build();
        //     studioBuilder.add(studioJsonObject);
        // }
        // for (String genre : anime.getGenres()) {
        //     JsonObject genreJsonObject = Json.createObjectBuilder()
        //                                         .add("name",genre)
        //                                         .build();
        //     genreBuilder.add(genreJsonObject);
        // }
        
        // JsonObject animeJsonObject = Json.createObjectBuilder()
        //                                  .add("mal_id",anime.getMal_id())
        //                                  .add("large_image_url",anime.getLarge_image_url())
        //                                  .add("title",anime.getTitle())
        //                                  .add("title_japanese",anime.getTitle_japanese())
        //                                  .add("type",anime.getType())
        //                                  .add("episodes",anime.getEpisodes())
        //                                  .add("status",anime.getStatus())
        //                                  .add("duration",anime.getDuration())
        //                                  .add("score",anime.getScore())
        //                                  .add("rank",anime.getRank())
        //                                  .add("synopsis",anime.getSynopsis())
        //                                  .add("year",anime.getYear())
        //                                  .add("producers",producerBuilder.build())
        //                                  .add("studios",studioBuilder.build())
        //                                  .add("genres",genreBuilder.build())
        //                                  .build();
        // animeRepo.setHash(category, String.valueOf(anime.getMal_id()), animeJsonObject.toString());

    }

    // public void saveArticles2(List<News> savedArticles) {
        
    //     for (News news : savedArticles) {
    //         JsonObject newsJsonObject = Json.createObjectBuilder()
    //                                         .add("id",news.getId())
    //                                         .add("published",news.getPublished())
    //                                         .add("title",news.getTitle())
    //                                         .add("url",news.getUrl())
    //                                         .add("imageurl",news.getImageUrl())
    //                                         .add("body",news.getBody())
    //                                         .add("tags",news.getTags())
    //                                         .add("categories",news.getCategories())
    //                                         .build();
    //         //if update, it will just autoupdate
    //         newsRepo.setHash(ConstantVar.redisKey, news.getId(), newsJsonObject.toString());                                 

    //     }
        
        
    // }

    public List<Anime> getAnimeListByQuery(String query) {
        
        System.out.println(query);
        String animeByQueryUrl = String.format(Url.animesByQuery,query);
        List<Anime> animeListByQuery = this.fetchAnimeApi(animeByQueryUrl);
        Set<String> allowedTypes = Set.of("tv", "movie", "ova","ona","tv_special");
        //for api errors
        if (!animeListByQuery.isEmpty()) {
            if ( animeListByQuery.getFirst().getTitle().equals("apierror") ) {
                return animeListByQuery;
            }
        }

    // Filter the anime list to include only allowed types
        // return animeListByQuery.stream()
        //     .filter(anime -> allowedTypes.contains(anime.getType()))
        //     .collect(Collectors.toList());
        // 
        return animeListByQuery.stream()
    .filter(anime -> anime.getType() != null && allowedTypes.contains(anime.getType().toLowerCase()))
    .collect(Collectors.toList());}
        // return animeListByQuery;}

    

    public List<Anime> getCachedAnimesByGenre(String genre) throws JsonMappingException, JsonProcessingException {
        List<Object> objectList = animeRepo.getAllValuesFromHash(genre);
        List<Anime> animes = new ArrayList<>();


        for (Object data : objectList) {
            String animeDataJsonString = (String) data;
            Anime anime = objectMapper.readValue(animeDataJsonString, Anime.class);
            animes.add(anime);
        }
        // for (Object data : objectList) {
        //     String dataJsonString = (String) data;
        //     InputStream is = new ByteArrayInputStream(dataJsonString.getBytes());
        //     JsonReader reader = Json.createReader(is);
        //     JsonObject dataJson = reader.readObject();
        //     Anime anime = new Anime();

        //     Integer mal_id = dataJson.getInt("mal_id");
        //     String large_image_url = dataJson.getString("large_image_url");
        //     String title = dataJson.getString("title");
        //     // List<String> genres = new ArrayList<>();
        //     // JsonArray genreJsonArray = dataJson.getJsonArray("genres");

        //     // for (int i = 0; i<genreJsonArray.size();i++) {
        //     //     JsonObject individualGenreJsonObject = genreJsonArray.getJsonObject(i);
        //     //     String genreName = individualGenreJsonObject.getString("name");
        //     //     genres.add(genreName);

        //     // }

        //     anime.setMal_id(mal_id);
        //     anime.setLarge_image_url(large_image_url);
        //     anime.setTitle(title);
        //     // anime.setGenres(genres);
        //     animes.add(anime);
            
        // }
        return animes;
    
    }
        
    

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
        } catch (RestClientException ex) {
            System.out.println(ex.getMessage());
            Anime anime = new Anime();
            anime.setTitle("apierror");
            animeList.add(anime);
            

        }
        
        return animeList;
    }
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
            // throw new Exception("cant get anime details");
            // return null;
            anime.setTitle("apierror");
        }
        return anime;
        

    }

    public Map<String, Integer> getAnimeGenreMap() {
        return animeGenreMap;
    }

    
    
    
    
}