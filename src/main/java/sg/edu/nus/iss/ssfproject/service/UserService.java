package sg.edu.nus.iss.ssfproject.service;

import java.util.ArrayList;
import java.util.List;

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


    


}
