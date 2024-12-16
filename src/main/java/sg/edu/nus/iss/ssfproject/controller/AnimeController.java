package sg.edu.nus.iss.ssfproject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.iss.ssfproject.models.Anime;
import sg.edu.nus.iss.ssfproject.models.User;
import sg.edu.nus.iss.ssfproject.service.AnimeService;


@Controller
public class AnimeController {
    
    @Autowired
    AnimeService animeService;
    
    
    
    @GetMapping("/")
    public String topAnimeByGenre(@RequestParam(required=false,name = "genre",defaultValue="Slice of Life") String genre,
     Model model,HttpSession session) {
        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);
        List<String> animeGenres = animeService.getAnimeGenres();
        model.addAttribute("animegenres",animeGenres);
        
        // if (genre ==null) {
        //     genre = "Slice of Life"; // default value
        // }
        List<Anime> animeListByGenre = animeService.getAnimeListByGenre(genre);
        model.addAttribute("animelist",animeListByGenre);
        model.addAttribute("selectedgenre",genre);
        return "view0";
    }
     @PostMapping("/filter")
    public String filterTaskByStatus(@RequestParam(required=false,name="genre",defaultValue="Slice of Life") String genre
    ,Model model,HttpSession session)  {

        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);
        List<String> animeGenres = animeService.getAnimeGenres();
        model.addAttribute("animegenres",animeGenres);
        List<Anime> animeListByGenre = animeService.getAnimeListByGenre(genre);
        model.addAttribute("animelist",animeListByGenre);
        model.addAttribute("selectedgenre",genre);
        System.out.println(genre);
        

        return "view0"; //cannot redirect here because it will go back to the original
    }
    @GetMapping("/filter/{genrename}")
    public String filterTaskByGenre(@PathVariable("genrename") String genre
    ,Model model,HttpSession session)  {

        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);
        List<String> animeGenres = animeService.getAnimeGenres();
        model.addAttribute("animegenres",animeGenres);
        List<Anime> animeListByGenre = animeService.getAnimeListByGenre(genre);
        model.addAttribute("animelist",animeListByGenre);
        model.addAttribute("selectedgenre",genre);
        System.out.println(genre);
        

        return "view0"; //cannot redirect here because it will go back to the original
    }

    @GetMapping("/search")
    public String showSearchPage(Model model,HttpSession session) {
        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);
        return "view1B";
    }

    @PostMapping("/searchresult")
    public String showAnime(@RequestParam String query,Model model,HttpSession session) {
        if (!query.matches("^[a-zA-Z0-9].*")) {
            model.addAttribute("errorMessage","Invalid input");
            return "view1B";
        }
        List<Anime> animeListByQuery = animeService.getAnimeListByQuery(query);
       
        model.addAttribute("animelist",animeListByQuery);
        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);

        return "view1B";
    }

    
    

}
