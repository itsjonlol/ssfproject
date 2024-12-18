package sg.edu.nus.iss.ssfproject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

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
    HttpSession session) {
        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);
        Anime anime = animeService.getAnimeById(id);
        model.addAttribute("anime",anime);
        Boolean animeInWatchList;
        if (verifiedUser == null) {
            animeInWatchList = false;
            model.addAttribute("animeinwatchlist",animeInWatchList);
        } else {
            animeInWatchList = userService.animeInUserWatchList(anime, verifiedUser);
            model.addAttribute("animeinwatchlist",animeInWatchList);

        }
       

        return "view2C";
    }

    @GetMapping("/addanime/{animeid}")
    public String addAnimeToWatchList(@PathVariable("animeid") String id,HttpSession session
    ,Model model) throws JsonMappingException, JsonProcessingException {
        Anime anime = animeService.getAnimeById(id); // can probably cache it
        model.addAttribute("anime",anime);
        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);
        
        //required to login before adding to wishlist
        if (session.getAttribute("verifieduser") == null) {
            model.addAttribute("errorMessage","Please login first before adding to watchlist");
            return "view2C";
        }
        //if logged in
        model.addAttribute("animeinwatchlist",userService.animeInUserWatchList(anime, verifiedUser));
        
        
        if (userService.animeInUserWatchList(anime, verifiedUser)) {
            model.addAttribute("animeinwatchlist", true);
        }
        userService.addAnimeToWatchList(anime,verifiedUser);
        model.addAttribute("animeinwatchlist", true);
        
        //to change "add to watchlist button to added to watchlist"

        // 1) is logged in
        // 2) have the anime on its watchlist


    
        return "view2C";
    }
    @GetMapping("/watchlist/{verifiedusername}")
    public String seeUserWatchList(@PathVariable("verifiedusername") String username,HttpSession session
    ,Model model) {

        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);

        if (verifiedUser == null ||!verifiedUser.getUsername().toLowerCase().equals(username.toLowerCase())) {
            return "invalidusererror";
        } // if someone tries to go to the watchlist being logged in

        List<Anime> verifiedUserWatchList = verifiedUser.getWatchListAnime();
        
        //need restcontroller to see someone else's watchlist..?

        //need to account for other's watchlist
        model.addAttribute("torecommend",true);
        //for the case of verified user seeing his watchlist
        
        model.addAttribute("watchlist",verifiedUserWatchList);
        

        return "watchlist";
    }
    @GetMapping("/watchlist/recommend/{verifiedusername}")
    public String getRecommendedAnime(@PathVariable("verifiedusername") String username,HttpSession session
    ,Model model) throws JsonProcessingException {

        //need restcontroller to see someone else's watchlist..?

        //need to account for other's watchlist

        //for the case of verified user seeing his watchlist
        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);

        if (verifiedUser == null ||!verifiedUser.getUsername().toLowerCase().equals(username.toLowerCase())) {
            return "invalidusererror";
        } // if someone tries to go to the watchlist being logged in
        List<Anime> verifiedUserWatchList = verifiedUser.getWatchListAnime();

        model.addAttribute("watchlist",verifiedUserWatchList);
        
        List<Anime> recommendedAnimeList = userService.recommendAnimeForUser(verifiedUser);
        model.addAttribute("recommendedlist",recommendedAnimeList);
        model.addAttribute("torecommend",true);
        

        return "watchlist";
    }

    @GetMapping("/deleteanime/{animeid}")
    public String deleteAnimeInWatchList(@PathVariable("animeid") String id,HttpSession session
    ,Model model) throws JsonMappingException, JsonProcessingException {
        Anime anime = animeService.getAnimeById(id); 
        
        User verifiedUser = (User) session.getAttribute("verifieduser");
        model.addAttribute("verifieduser",verifiedUser);
        List<Anime> verifiedUserWatchList = verifiedUser.getWatchListAnime();
        model.addAttribute("watchlist",verifiedUserWatchList);
        
        userService.deleteAnimeInWatchList(anime, verifiedUser);
        model.addAttribute("torecommend",true);
        
        


    
        return "watchlist";
    }

    
    
}
