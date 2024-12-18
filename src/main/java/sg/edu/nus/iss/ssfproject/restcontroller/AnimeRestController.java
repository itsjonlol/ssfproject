package sg.edu.nus.iss.ssfproject.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Anime> getTopAnimeByGenre(@RequestParam(required=false,name = "genre",defaultValue="Slice of Life") String genre) throws JsonProcessingException{
        List<Anime> animeListByGenre = animeService.getAnimeListByGenre(genre);
        return animeListByGenre;
    }

    //e.g. /api/search?query=attack+on+titan
    @GetMapping("/search")
    public List<Anime> getSearchedAnime(@RequestParam String query) {
        List<Anime> animeListByQuery = animeService.getAnimeListByQuery(query);
        return animeListByQuery;
    }
    
    
}
