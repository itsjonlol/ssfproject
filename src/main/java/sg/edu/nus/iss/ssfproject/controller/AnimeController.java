package sg.edu.nus.iss.ssfproject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.iss.ssfproject.models.Anime;
import sg.edu.nus.iss.ssfproject.models.User;
import sg.edu.nus.iss.ssfproject.service.AnimeService;


@Controller
public class AnimeController {
    
    @Autowired
    AnimeService animeService;
    
    @GetMapping("/")
    public String getLanding(Model model,HttpSession session) throws JsonProcessingException {
        User verifiedUser = (User) session.getAttribute("verifieduser");
        //update nav bar to show whether a user is logged in
        model.addAttribute("verifieduser",verifiedUser);
        //update url so that user can go back to this page post-login
        session.setAttribute("redirectUrl", "/");
        
        return "landing";
    }
    
    
    
    @GetMapping("/topanimes")
    public String topAnimeByGenre(@RequestParam(required=false,name = "genre",defaultValue="Slice of Life") String genre,
     Model model,HttpSession session) throws JsonProcessingException {
        //update nav bar to show whether a user is logged in
        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);
        //get list of approved genres to be displayed in front end.
        List<String> animeGenres = animeService.getAnimeGenresForFrontPage();
        
        model.addAttribute("animegenres",animeGenres);
        
        
        List<Anime> animeListByGenre = animeService.getAnimeListByGenre(genre);
        //account for api errors
        if (!animeListByGenre.isEmpty()) {
            if ( animeListByGenre.getFirst().getTitle().equals("apierror") ) {
                return "error";
            }
        }
        model.addAttribute("animelist",animeListByGenre);
        model.addAttribute("selectedgenre",genre);
         //update url so that user can go back to this page post-login
        session.setAttribute("redirectUrl", "/topanimes");
        return "topanimes";
    }
    
    @GetMapping("/filter/{genrename}")
    public String filterTaskByGenre(@PathVariable("genrename") String genre
    ,Model model,HttpSession session) throws JsonProcessingException  {
        //update nav bar to show whether a user is logged in
        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);
         //get list of approved genres to be displayed in front end.
        List<String> animeGenres = animeService.getAnimeGenresForFrontPage();
        model.addAttribute("animegenres",animeGenres);
        //if genre doesnt exist
        if (!animeGenres.contains(genre)) {
            return "error";
        }
        List<Anime> animeListByGenre = animeService.getAnimeListByGenre(genre);
        
         //for api error cases
        if (!animeListByGenre.isEmpty()) {
            if ( animeListByGenre.getFirst().getTitle().equals("apierror") ) {
                return "error";
            }
        }
        model.addAttribute("animelist",animeListByGenre);
        model.addAttribute("selectedgenre",genre);
        System.out.println(genre);
         //update url so that user can go back to this page post-login
        session.setAttribute("redirectUrl", "/filter/"+ genre);
        return "topanimes"; 
    }

    @GetMapping("/search")
    public String showSearchPage(Model model,HttpSession session) {
         //update nav bar to show whether a user is logged in
        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);
        //update url so that user can go back to this page post-login
        session.setAttribute("redirectUrl", "/search");
        return "search";
    }

    @PostMapping("/searchresult")
    public String showAnime(@RequestParam String query,Model model,HttpSession session) {
        
        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);

        //to ensure doesnt start with special characters.
        if (!query.matches("^[a-zA-Z0-9].*")) {
            model.addAttribute("errorMessage","Invalid input. Please start with an alphanumeric character");
            return "search";
        }
        
        //get search results
        List<Anime> animeListByQuery = animeService.getAnimeListByQuery(query);
        //for api error cases
        if (!animeListByQuery.isEmpty()) {
            if ( animeListByQuery.getFirst().getTitle().equals("apierror") ) {
                return "error";
            }
        }
        //display appropriate message should there be no search results
        if (animeListByQuery.isEmpty()) {
            model.addAttribute("noresults",true);
        }
       
        model.addAttribute("animelist",animeListByQuery);
        

        return "search";
    }

    
    

}
