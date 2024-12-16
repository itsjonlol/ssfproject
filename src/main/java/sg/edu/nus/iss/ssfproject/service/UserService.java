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
    public void deleteAnimeInWatchList(Anime anime, User verifiedUser) throws JsonProcessingException {

        List<Anime> watchListAnime = verifiedUser.getWatchListAnime();
        if (watchListAnime == null) {
            watchListAnime = new ArrayList<>();
        } 

        //delete anime from watchlist
        watchListAnime.remove(anime);

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
    public List<Anime> recommendAnimeForUser(User verifiedUser) throws JsonProcessingException {

        // List<Anime> recommendedAnimeList = verifiedUser.getWatchListAnime();

        List<Anime> finalRecommendedAnimeList;
        List<Anime> watchListAnime = verifiedUser.getWatchListAnime();
        if (watchListAnime == null) {
            watchListAnime = new ArrayList<>();
        } 
        // if (recommendedAnimeList == null) {
        //     recommendedAnimeList= new ArrayList<>();
        // } 

        
        //algorithm 1: pick random 2 genres if size of watchlist is less than 5
        if (watchListAnime.size() <= 5) {
            //get list of genres
            Set<Object> objects = userRepo.getAllKeysFromHash(ConstantVar.genresRedisKey);
            List<String> keys = new ArrayList<>();
            for (Object object : objects) {
                String key = (String) object;   
                keys.add(key);
                
            }
            Random random = new Random();
            List<String> randomGenres = new ArrayList<>();
            // List<String> randomGenreIds = new ArrayList<>();
            //add 2 random genres into the list
            // System.out.println(keys.size());
            randomGenres.add(keys.get(random.nextInt(keys.size())));
            randomGenres.add(keys.get(random.nextInt(keys.size())));

            // for ( String randomGenre : randomGenres) {
            //     System.out.println("Users random genre: " + randomGenre);
            //     String genreId = animeRepo.getValueFromHash(ConstantVar.genresRedisKey,randomGenre);
            //     randomGenreIds.add(genreId);
            //     System.out.println("random genreid: " + genreId);
    
            // }
        
            // get a recommended anime list from the 2 random genres
            finalRecommendedAnimeList = getRecommendedListFromGenreIds(randomGenres, verifiedUser);
            // finalRecommendedAnimeList = null;
        } else {
            //algorithm 2: recommend anime based on the top 3 genre of 5 random anime in user's watchlist
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
        // String favGenre = genreCount.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        //Get users top 2 favourite genres
        List<String> favGenres = genreCount.entrySet()
                                           .stream()
                                           .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                                           .limit(2).map(Map.Entry::getKey)
                                           .collect(Collectors.toList());
        
        // List<String> favGenreIds = new ArrayList<>();
        // for ( String favGenre : favGenres) {
        //     System.out.println("Users fav genre: " + favGenre);
        //     String genreId = animeRepo.getValueFromHash(ConstantVar.genresRedisKey,favGenre);
        //     favGenreIds.add(genreId);
        //     System.out.println("favourite genreid: " + genreId);

        // }

        finalRecommendedAnimeList = getRecommendedListFromGenreIds(favGenres, verifiedUser);

        }
        
        // String animeByDiffGenreIdsUrl = String.format(Url.animesByDiffGenreIds,favGenreIds.get(0),favGenreIds.get(1));
        // System.out.println(animeByDiffGenreIdsUrl);
        // recommendedAnimeList = animeService.fetchAnimeApi(animeByDiffGenreIdsUrl);
        // List<Anime> finalRecommendedAnimeList = new ArrayList<>();


        // for (Anime anime : recommendedAnimeList) {
        //     // System.out.println(anime.toString());
        //     //want to ensure that the recommended list doesn't already contain the anime the user has already watched
        //     //get 10 recommendations max
        //     if (!watchListAnime.contains(anime) && finalRecommendedAnimeList.size() < 10) {
        //         finalRecommendedAnimeList.add(anime);
        //     }
        //     // finalRecommendedAnimeList.add(anime);
        // }

        // for (Anime anime : finalRecommendedAnimeList) {
        //     System.out.println(anime.toString());
        // }
        // System.out.println(finalRecommendedAnimeList.size());

        return finalRecommendedAnimeList;
    }

    private List<Anime> getRecommendedListFromGenreIds(List<String> genresList,User verifiedUser) throws JsonProcessingException {
        List<String> genreIdsList = new ArrayList<>();
        for ( String genre : genresList) {
            System.out.println("Users genre: " + genre);
            String genreId = animeRepo.getValueFromHash(ConstantVar.genresRedisKey,genre);
            genreIdsList.add(genreId);
            System.out.println("random genreid: " + genreId);

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
            // System.out.println(anime.toString());
            //want to ensure that the recommended list doesn't already contain the anime the user has already watched
            //get 10 recommendations max
            if (!watchListAnime.contains(anime) && finalRecommendedAnimeList.size() < 10) {
                finalRecommendedAnimeList.add(anime);
                // userRepo.setHash(ConstantVar.usersRedisKey, , animeByDiffGenreIdsUrl);
            }

            // finalRecommendedAnimeList.add(anime);
        }

        for (Anime anime : finalRecommendedAnimeList) {
            System.out.println(anime.toString());
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
