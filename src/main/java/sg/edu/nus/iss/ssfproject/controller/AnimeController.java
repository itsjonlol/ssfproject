package sg.edu.nus.iss.ssfproject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sg.edu.nus.iss.ssfproject.models.Anime;
import sg.edu.nus.iss.ssfproject.service.AnimeService;


@Controller
public class AnimeController {
    
    @Autowired
    AnimeService animeService;
    
    
    
    @GetMapping("/")
    public String topAnimeByGenre(@RequestParam(required=false,name = "genre",defaultValue="Slice of Life") String genre, Model model) {
        List<String> animeGenres = animeService.getAnimeGenres();
        model.addAttribute("animegenres",animeGenres);
        // if (genre ==null) {
        //     genre = "Slice of Life"; // default value
        // }
        List<Anime> animeListByGenre = animeService.getAnimeListByGenre(genre);
        model.addAttribute("animelist",animeListByGenre);
        return "view0";
    }
     @PostMapping("/filter")
    public String filterTaskByStatus(@RequestParam(required=false,name="genre",defaultValue="Slice of Life") String genre
    ,Model model)  {
        List<String> animeGenres = animeService.getAnimeGenres();
        model.addAttribute("animegenres",animeGenres);
        List<Anime> animeListByGenre = animeService.getAnimeListByGenre(genre);
        model.addAttribute("animelist",animeListByGenre);
        System.out.println(genre);
        

        return "view0"; //cannot redirect here because it will go back to the original
    }

    @GetMapping("/search")
    public String showSearchPage() {
        return "view1";
    }

    @PostMapping("/searchresult")
    public String showAnime(@RequestParam String query,Model model) {
        
        List<Anime> animeListByQuery = animeService.getAnimeListByQuery(query);
       
        model.addAttribute("animelist",animeListByQuery);

        return "view1";
    }

    @GetMapping("/{animeid}")
    public String getMethodName(@PathVariable("animeid") String id, Model model) {
        Anime anime = animeService.getAnimeById(id);
        model.addAttribute("anime",anime);

        return "view2B";
    }
    

}
