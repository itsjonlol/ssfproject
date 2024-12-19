package sg.edu.nus.iss.ssfproject.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sg.edu.nus.iss.ssfproject.models.Anime;
import sg.edu.nus.iss.ssfproject.service.AnimeService;


@RestController
@RequestMapping("/api")
public class AnimeRestController {
    @Autowired
    AnimeService animeService;
    
    //e.g. /api/topanimes?genre=Romance OR /api/topanimes?genre=Award+Winning  //category is case sensitive. cannot be /api/topanimes?genre=romance for e.g.
    @GetMapping("/topanimes") 
    public List<Anime> getTopAnimeByGenre(@RequestParam(required=false,name = "genre",defaultValue="Slice of Life") String genre) throws Exception{
        List<Anime> animeListByGenre = animeService.getAnimeListByGenre(genre);
        return animeListByGenre;
        
    }

    //e.g. /api/search?query=attack+on+titan
    @GetMapping("/search")
    public ResponseEntity<List<Anime>> getSearchedAnime(@RequestParam String query) throws Exception {
        try {
            List<Anime> animeListByQuery = animeService.getAnimeListByQuery(query);
            return ResponseEntity.status(200).header("Content-Type", "application/json").body(animeListByQuery);
        } catch (Exception e) {
            e.getMessage();
            return ResponseEntity.status(400).header("Content-Type", "application/json").body(null);
        }
        
        
    }
    
    
}
