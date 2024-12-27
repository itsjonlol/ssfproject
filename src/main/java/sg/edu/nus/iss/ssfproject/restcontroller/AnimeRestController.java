package sg.edu.nus.iss.ssfproject.restcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import sg.edu.nus.iss.ssfproject.models.Anime;
import sg.edu.nus.iss.ssfproject.service.AnimeService;


@RestController
@RequestMapping("/api")
public class AnimeRestController {
    @Autowired
    AnimeService animeService;
    
    //e.g. /api/topanimes?genre=Romance OR /api/topanimes?genre=Award+Winning  //category is case sensitive. cannot be /api/topanimes?genre=romance for e.g.
    @GetMapping("/topanimes") 
    public ResponseEntity<?> getTopAnimeByGenre(@RequestParam(required=false,name = "genre",defaultValue="Slice of Life") String genre) throws JsonProcessingException{
        
        //for the case of writing a genre that doesnt exist. must have capitalised first letter.
        if (!animeService.getAnimeGenreMap().containsKey(genre)) {

            Map<String,String> errorMessage = new HashMap<>();
            errorMessage.put("Error Message","Genre not found. Ensure that first letter of each word is capitalised. E.g. Award+Winning OR Romance");
            return ResponseEntity.status(404).header("Content-Type", "application/json").body(errorMessage);
        }
        
        List<Anime> animeListByGenre = animeService.getAnimeListByGenre(genre);
        //to handle api errors
        if (!animeListByGenre.isEmpty()) {
            if ( animeListByGenre.getFirst().getTitle().equals("apierror") ) {
                animeListByGenre = new ArrayList<>();
                return ResponseEntity.status(400).header("Content-Type", "application/json").body(animeListByGenre);
            }
        }
        return ResponseEntity.status(200).header("Content-Type", "application/json").body(animeListByGenre);
    }

    //e.g. /api/search?query=attack+on+titan
    @GetMapping("/search")
    public ResponseEntity<?> getSearchedAnime(@RequestParam String query) {

       

        List<Anime> animeListByQuery = animeService.getAnimeListByQuery(query);
        if (!animeListByQuery.isEmpty()) {
            //handle api errors
            if ( animeListByQuery.getFirst().getTitle().equals("apierror") ) {
                animeListByQuery = new ArrayList<>();
                return ResponseEntity.status(400).header("Content-Type", "application/json").body(animeListByQuery);
            }
        } else {
            //for cases where query gives 0 results
            Map<String,String> message = new HashMap<>();
            message.put("Message","No anime of the title ' " + query + " ' can be found at the moment.");
            return ResponseEntity.status(404).header("Content-Type", "application/json").body(message);

        }

        return ResponseEntity.status(200).header("Content-Type", "application/json").body(animeListByQuery);
       
    }
    
    
}
