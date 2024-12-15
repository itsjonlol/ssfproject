package sg.edu.nus.iss.ssfproject.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.edu.nus.iss.ssfproject.constant.ConstantVar;
import sg.edu.nus.iss.ssfproject.models.Anime;
import sg.edu.nus.iss.ssfproject.models.User;
import sg.edu.nus.iss.ssfproject.repo.UserRepo;

@Service
public class UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    private ObjectMapper objectMapper;

    public void addAnimeToWatchList(Anime anime,User verifiedUser) throws JsonMappingException, JsonProcessingException {
        // TODO Auto-generated method stub
        // String userJsonString = userRepo.getValueFromHash(ConstantVar.usersRedisKey,verifiedUser.getUsername().trim().toLowerCase());
        
        // User user = objectMapper.readValue(userJsonString, User.class);
        List<Anime> watchListAnime = verifiedUser.getWatchListAnime();
        if (watchListAnime == null) {
            watchListAnime = new ArrayList<>();
        } 

        
        if (!watchListAnime.contains(anime)) {
                watchListAnime.add(anime);
        }
        
        //update watchlistanime
        verifiedUser.setWatchListAnime(watchListAnime);
        //update current session user details
        

        //update userjsonstring and store it back into redis
        String userJsonString = objectMapper.writeValueAsString(verifiedUser);
        
        userRepo.setHash(ConstantVar.usersRedisKey,verifiedUser.getUsername().trim().toLowerCase(), userJsonString);
        
        
    }

    public Boolean animeInUserWatchList(Anime anime,User verifiedUser) { 

        List<Anime> watchListAnime = verifiedUser.getWatchListAnime();
        if (watchListAnime == null) {
            watchListAnime = new ArrayList<>();
        } 
        return watchListAnime.contains(anime);

    }


    //recommends anime based on user's watchlist
    public List<Anime> recommendAnimeForUser(User verifiedUser) {

        List<Anime> recommendedAnimeList = verifiedUser.getWatchListAnime();

        
        List<Anime> watchListAnime = verifiedUser.getWatchListAnime();
        if (watchListAnime == null) {
            watchListAnime = new ArrayList<>();
        } 
        if (recommendedAnimeList == null) {
            recommendedAnimeList= new ArrayList<>();
        } 
        //algorithm 1: recommend generic top anime if size of watchlist is less than 5
        // if (watchListAnime.size() <= 5) {
            
        // }
        //algorithm 2: recommend anime based on the top genre of 5 random anime in user's watchlist
        List<Anime> shuffledList = new ArrayList<>(watchListAnime);
        Collections.shuffle(shuffledList);

        //get the list of 5 random anime from watchlist
        List<Anime> first5AnimeRandomList = shuffledList.stream().limit(5).collect(Collectors.toList());
        Map<String,Integer> genreCount = new HashMap<>();

        for (Anime anime : first5AnimeRandomList) {
            List<String> animeGenres = anime.getGenres();
            for (String genre : animeGenres) {
                Integer count = genreCount.get(genre);
                if (count == null) {
                    genreCount.put(genre, 1);
                } else {
                    genreCount.put(genre,count+1);
                }
            }
        }
        String favGenre = genreCount.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);

        System.out.println(favGenre);
        
        return recommendedAnimeList;
    }


    


}
