package sg.edu.nus.iss.ssfproject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.iss.ssfproject.models.Anime;
import sg.edu.nus.iss.ssfproject.models.User;
import sg.edu.nus.iss.ssfproject.service.AnimeService;
import sg.edu.nus.iss.ssfproject.service.UserService;



@Controller
public class UserController {
    @Autowired
    AnimeService animeService;
    
    @Autowired
    UserService userService;

    @GetMapping("/{animeid}")
    public String showIndividualAnimePage(@PathVariable("animeid") String id, Model model,
    HttpSession session) throws Exception {
        //update nav bar to show whether a user is logged in
        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);

        //retrieve the anime to be displayed
        Anime anime = animeService.getAnimeById(id);
        
        //should there be api errors
        if (anime.getTitle().equals("apierror")) {
            return "error";
        }

        model.addAttribute("anime",anime);
        
        //ensure it is "add to watchlist" if  a) no user is logged in and or b) not in user's watchlist
        Boolean animeInWatchList;
        if (verifiedUser == null) {
            animeInWatchList = false;
            model.addAttribute("animeinwatchlist",animeInWatchList);
        } else {
            animeInWatchList = userService.animeInUserWatchList(anime, verifiedUser);
            model.addAttribute("animeinwatchlist",animeInWatchList);

        }
        //update url so that user can go back to this page post-login
        session.setAttribute("redirectUrl", "/"+id);
  
    
        return "view2C";
    }
    //function to add anime for a user
    @GetMapping("/addanime/{animeid}")
    public String addAnimeToWatchList(@PathVariable("animeid") String id,HttpSession session
    ,Model model) throws Exception {
        
        //retrieve the anime to be displayed
        Anime anime = animeService.getAnimeById(id); 
        model.addAttribute("anime",anime);
        //update nav bar to show whether a user is logged in
        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);
        
        //required to login before adding to wishlist
        if (session.getAttribute("verifieduser") == null) {
            model.addAttribute("errorMessage","Please login first before adding to watchlist");
            session.setAttribute("redirectUrl", "/"+id);
            return "view2C";
        }
        //if logged in. 
        model.addAttribute("animeinwatchlist",userService.animeInUserWatchList(anime, verifiedUser));
        
        //update to " added to watchlist " once user adds the anime into their watchlist / or is already added
        if (userService.animeInUserWatchList(anime, verifiedUser)) {
            model.addAttribute("animeinwatchlist", true);
        }
        userService.addAnimeToWatchList(anime,verifiedUser);
        model.addAttribute("animeinwatchlist", true);
        
        

    
        return "view2C";
    }

    @GetMapping("/watchlist/{verifiedusername}")
    public String seeUserWatchList(@PathVariable("verifiedusername") String username,HttpSession session
    ,Model model) {

        //update nav bar to show whether a user is logged in
        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);

        // if one tries to go to another person's watchlist, reject them
        if (verifiedUser == null ||!verifiedUser.getUsername().toLowerCase().equals(username.toLowerCase())) {
            return "invalidusererror";
        } 

        //retrieve user's watchlist
        List<Anime> verifiedUserWatchList = verifiedUser.getWatchListAnime();
        
        model.addAttribute("torecommend",true);
        
        model.addAttribute("watchlist",verifiedUserWatchList);
        

        return "watchlist";
    }
    @GetMapping("/watchlist/recommend/{verifiedusername}")
    public String getRecommendedAnime(@PathVariable("verifiedusername") String username,HttpSession session
    ,Model model) throws JsonProcessingException {

        
        //for the case of verified user seeing his watchlist
        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);

         // if one tries to go to another person's watchlist, reject them
        if (verifiedUser == null ||!verifiedUser.getUsername().toLowerCase().equals(username.toLowerCase())) {
            return "invalidusererror";
        } 
        //retrieve user's watchlist
        List<Anime> verifiedUserWatchList = verifiedUser.getWatchListAnime();
        
        model.addAttribute("watchlist",verifiedUserWatchList);
        
        //retrieve user's recommended watchlist
        List<Anime> recommendedAnimeList = userService.recommendAnimeForUser(verifiedUser);

        //for api error cases
        if (!recommendedAnimeList.isEmpty()) {
            if (recommendedAnimeList.getFirst().getTitle().equals("apierror") ) {
                return "error";
            }
        }
        // if unable to retrieve recommended watchlist
        if (recommendedAnimeList.isEmpty()) {
            model.addAttribute("errorMessage","Unable to recommended anime list at the moment.");
            
        }
        
        model.addAttribute("recommendedlist",recommendedAnimeList);
        model.addAttribute("torecommend",true);
        

        return "watchlist";
    }

    @GetMapping("/deleteanime/{animeid}")
    public String deleteAnimeInWatchList(@PathVariable("animeid") String id,HttpSession session
    ,Model model) throws Exception {

        //retrieve anime based on its id
        Anime anime = animeService.getAnimeById(id); 
        
        //retrieve the current user
        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);

        //display current watchlist
        List<Anime> verifiedUserWatchList = verifiedUser.getWatchListAnime();
        model.addAttribute("watchlist",verifiedUserWatchList);
        
        userService.deleteAnimeInWatchList(anime, verifiedUser);
        model.addAttribute("torecommend",true);
        
        return "watchlist";
    }

    
    
}
