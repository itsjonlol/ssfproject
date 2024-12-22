package sg.edu.nus.iss.ssfproject.util;

import java.util.ArrayList;
import java.util.List;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.ssfproject.models.Anime;

public class AnimeUtil {
    //parse the json from an api call to construct an anime object.
    public static  Anime parseJsonObject(JsonObject jObject) {

        Anime anime = new Anime();
        //retrive the relevant data from the call

        //single attributes are null if no value
        //while those with array shows empty array if no value

        //put null values for each attribute if it is null
        //quote from jikan api webiste
        /*
         * Any property (except arrays or objects) whose value does not exist or is undetermined, will be null.
           Any array or object property whose value does not exist or is undetermined, will be empty
         */
        Integer mal_id = jObject.getInt("mal_id"); // anime will always have a mal_id
        String large_image_url = jObject.getJsonObject("images").getJsonObject("webp").getString("large_image_url",null);
        String title = jObject.getString("title",null);
        String title_japanese = jObject.getString("title_japanese", null); // Handle null values
        String type = jObject.getString("type", null);
        Integer episodes = (!jObject.isNull("episodes")) ? jObject.getInt("episodes") : null;
        String status = jObject.getString("status", null);
        String duration = jObject.getString("duration", null);
        Double score = (!jObject.isNull("score")) ? jObject.getJsonNumber("score").doubleValue() : null;
        Integer rank = (!jObject.isNull("rank")) ? jObject.getInt("rank") : null ;
        String synopsis = jObject.getString("synopsis", null);
        Integer year = (!jObject.isNull("year")) ? jObject.getInt("year") : null;

        JsonArray producerArray = (!jObject.isNull("producers")) ? jObject.getJsonArray("producers") : null;
        JsonArray studioArray = (!jObject.isNull("studios")) ? jObject.getJsonArray("studios") : null;
        JsonArray genreArray = (!jObject.isNull("genres")) ? jObject.getJsonArray("genres") : null ;


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
