package sg.edu.nus.iss.ssfproject.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.edu.nus.iss.ssfproject.constant.ConstantVar;
import sg.edu.nus.iss.ssfproject.constant.Url;
import sg.edu.nus.iss.ssfproject.models.Anime;
import sg.edu.nus.iss.ssfproject.models.User;
import sg.edu.nus.iss.ssfproject.repo.AnimeRepo;
import sg.edu.nus.iss.ssfproject.repo.UserRepo;

@Service
public class UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    AnimeService animeService;

    @Autowired
    AnimeRepo animeRepo;

    
    public void addAnimeToWatchList(Anime anime,User verifiedUser) throws JsonMappingException, JsonProcessingException {
        
        
        List<Anime> watchListAnime = verifiedUser.getWatchListAnime();

        //prevent null-pointer
        if (watchListAnime == null) {
            watchListAnime = new ArrayList<>();
        } 

        //add a non-duplicate anime into the watchlist
        if (!watchListAnime.contains(anime)) {
                watchListAnime.add(anime);
        }
        
        //update watchlistanime
        verifiedUser.setWatchListAnime(watchListAnime);
    
        
        //update userjsonstring and store it back into redis
        String userJsonString = objectMapper.writeValueAsString(verifiedUser);
        
        userRepo.setHash(ConstantVar.usersRedisKey,verifiedUser.getUsername().trim().toLowerCase(), userJsonString);
        
        
    }
    public void deleteAnimeInWatchList(Anime anime, User verifiedUser) throws JsonProcessingException {

        List<Anime> watchListAnime = verifiedUser.getWatchListAnime();
        //prevent null-pointer
        if (watchListAnime == null) {
            watchListAnime = new ArrayList<>();
        } 

        //delete anime from watchlist
        watchListAnime.remove(anime);

         //update watchlistanime
         verifiedUser.setWatchListAnime(watchListAnime);
         
         //update userjsonstring and store it back into redis
         String userJsonString = objectMapper.writeValueAsString(verifiedUser);
         
         userRepo.setHash(ConstantVar.usersRedisKey,verifiedUser.getUsername().trim().toLowerCase(), userJsonString);

    }

    //check existence of anime in a particular user's watchlist
    public Boolean animeInUserWatchList(Anime anime,User verifiedUser) { 

        List<Anime> watchListAnime = verifiedUser.getWatchListAnime();
        if (watchListAnime == null) {
            watchListAnime = new ArrayList<>();
        } 
        return watchListAnime.contains(anime);

    }


    //recommends anime based on user's watchlist
    public List<Anime> recommendAnimeForUser(User verifiedUser) throws JsonProcessingException {

       

        List<Anime> finalRecommendedAnimeList;
        List<Anime> watchListAnime = verifiedUser.getWatchListAnime();
        if (watchListAnime == null) {
            watchListAnime = new ArrayList<>();
        } 
       
    
        //algorithm 1: pick random 2 genres if size of watchlist is less than 5
        if (watchListAnime.size() <= 5) {
            //get list of genres in approved genre list stored in redis
            Map<String,Integer> animeGenreMap = animeService.getAnimeGenreMap();
            Set<String> keySet = animeGenreMap.keySet();
            List<String> keys = new ArrayList<>();
            for (String key : keySet) {
                
                keys.add(key);
                System.out.println("The map key is + " + key);
                
            }
           
            Random random = new Random();
            List<String> randomGenres = new ArrayList<>();
            
            //add 2 random genres into the list
            randomGenres.add(keys.get(random.nextInt(keys.size())));
            randomGenres.add(keys.get(random.nextInt(keys.size())));

        
            // get a recommended anime list from the 2 random genres
            finalRecommendedAnimeList = getRecommendedListFromGenreIds(randomGenres, verifiedUser);
            
        } else {
            //algorithm 2: recommend anime based on the top 2 genres of 5 random anime in user's watchlist
        List<Anime> shuffledList = new ArrayList<>(watchListAnime);
        Collections.shuffle(shuffledList);

        //get the list of 5 random anime from watchlist
        List<Anime> first5AnimeRandomList = shuffledList.stream().limit(5).collect(Collectors.toList());
        Map<String,Integer> genreCount = new HashMap<>();

        //put the anime genres in a map. map the genre name to its count.
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
        // String favGenre = genreCount.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        //Get users top 2 favourite genres and store it into a list
        List<String> favGenres = genreCount.entrySet()
                                           .stream()
                                           .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                                           .limit(2).map(Map.Entry::getKey)
                                           .collect(Collectors.toList());

        finalRecommendedAnimeList = getRecommendedListFromGenreIds(favGenres, verifiedUser);

        }
        
        return finalRecommendedAnimeList;
    }
    //helper function to get a list of recommended anime based on 2 genres (either random or the top genres from a user's watchlist)
    private List<Anime> getRecommendedListFromGenreIds(List<String> genresList,User verifiedUser) throws JsonProcessingException {
        List<String> genreIdsList = new ArrayList<>();
        for ( String genre : genresList) {
            System.out.println("Users genre: " + genre);
            Map<String,Integer> animeGenreMap = animeService.getAnimeGenreMap();
            String genreId = String.valueOf(animeGenreMap.get(genre));
           
            genreIdsList.add(genreId);

        }
        List<Anime> finalRecommendedAnimeList = new ArrayList<>();
        List<Anime> watchListAnime = verifiedUser.getWatchListAnime();
        if (watchListAnime == null) {
            watchListAnime = new ArrayList<>();
        } 

        String animeByDiffGenreIdsUrl = String.format(Url.animesByDiffGenreIds,genreIdsList.get(0),genreIdsList.get(1));
        System.out.println(animeByDiffGenreIdsUrl);
        List<Anime> recommendedAnimeList = animeService.fetchAnimeApi(animeByDiffGenreIdsUrl);
        
        for (Anime anime : recommendedAnimeList) {
            System.out.println(anime.toString());
            //want to ensure that the recommended list doesn't already contain the anime the user has already watched
            //get 20 recommendations max
            if (!watchListAnime.contains(anime) && finalRecommendedAnimeList.size() < 21) {
                finalRecommendedAnimeList.add(anime);
                // userRepo.setHash(ConstantVar.usersRedisKey, , animeByDiffGenreIdsUrl);
            }

        }

        System.out.println(finalRecommendedAnimeList.size());
       
        //update current session user details
        verifiedUser.setRecommendedAnime(finalRecommendedAnimeList);
        
        //update userjsonstring and store it back into redis
        String userJsonString = objectMapper.writeValueAsString(verifiedUser);
        
        userRepo.setHash(ConstantVar.usersRedisKey,verifiedUser.getUsername().trim().toLowerCase(), userJsonString);

        return finalRecommendedAnimeList;
    }

    
}
