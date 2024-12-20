package sg.edu.nus.iss.ssfproject.restcontroller;

import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<List<Anime>> getTopAnimeByGenre(@RequestParam(required=false,name = "genre",defaultValue="Slice of Life") String genre) throws JsonProcessingException{
        List<Anime> animeListByGenre = animeService.getAnimeListByGenre(genre);
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
    public ResponseEntity<List<Anime>> getSearchedAnime(@RequestParam String query) {
        List<Anime> animeListByQuery = animeService.getAnimeListByQuery(query);
        if (!animeListByQuery.isEmpty()) {
            if ( animeListByQuery.getFirst().getTitle().equals("apierror") ) {
                animeListByQuery = new ArrayList<>();
                return ResponseEntity.status(400).header("Content-Type", "application/json").body(animeListByQuery);
            }
        }
        return ResponseEntity.status(200).header("Content-Type", "application/json").body(animeListByQuery);
       
    }
    
    
}
