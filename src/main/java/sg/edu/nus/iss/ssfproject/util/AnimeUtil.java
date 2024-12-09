package sg.edu.nus.iss.ssfproject.util;

import java.util.ArrayList;
import java.util.List;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.ssfproject.models.Anime;

public class AnimeUtil {
    public static  Anime parseJsonObject(JsonObject jObject) {

        Anime anime = new Anime();
        //retrive the relevant data from the call

        // if (jObject.isNull("episodes")) {
//     episodes = null;
// } else {
//     episodes = jObject.getInt("episodes");
// }
        Integer mal_id = jObject.getInt("mal_id");
        String large_image_url = jObject.getJsonObject("images").getJsonObject("webp").getString("large_image_url");
        String title = jObject.getString("title");
        String title_japanese = jObject.getString("title_japanese", null); // Handle null values
        String type = jObject.getString("type", null);
        Integer episodes = jObject.isNull("episodes") ? null : jObject.getInt("episodes");
        String status = jObject.getString("status", null);
        String duration = jObject.getString("duration", null);
        Double score = jObject.isNull("score") ? null : jObject.getJsonNumber("score").doubleValue();
        Integer rank = jObject.isNull("rank") ? null : jObject.getInt("rank");
        String synopsis = jObject.getString("synopsis", null);
        Integer year = jObject.isNull("year") ? null : jObject.getInt("year");

        JsonArray producerArray = jObject.getJsonArray("producers");
        JsonArray studioArray = jObject.getJsonArray("studios");
        JsonArray genreArray = jObject.getJsonArray("genres");

        List<String> producers = new ArrayList<>();
        List<String> studios = new ArrayList<>();
        List<String> genres = new ArrayList<>();

        if (producerArray != null) {
            for (int j = 0; j < producerArray.size(); j++) {
                producers.add(producerArray.getJsonObject(j).getString("name"));
            }
        }

        if (studioArray != null) {
            for (int j = 0; j < studioArray.size(); j++) {
                studios.add(studioArray.getJsonObject(j).getString("name"));
            }
        }

        if (genreArray != null) {
            for (int j = 0; j < genreArray.size(); j++) {
                genres.add(genreArray.getJsonObject(j).getString("name"));
            }
        }
        
        
        anime.setMal_id(mal_id);
        anime.setLarge_image_url(large_image_url);
        anime.setTitle(title);
        anime.setTitle_japanese(title_japanese);
        anime.setType(type);
        anime.setEpisodes(episodes);
        anime.setStatus(status);
        anime.setDuration(duration);
        anime.setScore(score);
        anime.setRank(rank);
        anime.setSynopsis(synopsis);
        anime.setYear(year);
        anime.setProducers(producers);
        anime.setStudios(studios);
        anime.setGenres(genres);

        return anime;

    }
}
