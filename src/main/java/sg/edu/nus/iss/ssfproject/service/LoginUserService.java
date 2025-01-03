package sg.edu.nus.iss.ssfproject.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.edu.nus.iss.ssfproject.constant.ConstantVar;
import sg.edu.nus.iss.ssfproject.models.User;
import sg.edu.nus.iss.ssfproject.repo.UserRepo;

@Service
public class LoginUserService {
    
    @Autowired
    UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    public void register(User user) throws JsonProcessingException {

        //store an encrypted version of user's password for security
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        //make User object into a Json String format
        String userJsonString = objectMapper.writeValueAsString(user);
        
        
        // Store the User in json-string format into redis
        
        userRepo.setHash(ConstantVar.usersRedisKey,user.getUsername().trim().toLowerCase(), userJsonString);
    }

    public Boolean checkIfUsernameExists(String username) {
        System.out.println(username.trim().toLowerCase());
        return userRepo.hasKey(ConstantVar.usersRedisKey, username.trim().toLowerCase());

    }
    public Boolean checkIfEmailExists(String email) throws JsonMappingException, JsonProcessingException {
        
        List<User> users = this.getUsers();
        
        for (User user : users) {
            if (email.toLowerCase().equals(user.getEmail().toLowerCase())) {
                return true;
            }
        }
        return false;

    }
    public List<User> getUsers() throws JsonMappingException, JsonProcessingException {
        List<Object> objects = userRepo.getAllValuesFromHash(ConstantVar.usersRedisKey);

        List<User> users = new ArrayList<>();

        for (Object object : objects) {
            String userJsonString = (String) object;
            //convert userjsonstring to user object
            User user = objectMapper.readValue(userJsonString, User.class);
            users.add(user);
        }

        return users;

    }

    public Boolean checkIfPasswordMatches(String username, String password) throws JsonProcessingException {
        
        String userJsonString = userRepo.getValueFromHash(ConstantVar.usersRedisKey,username.trim().toLowerCase());

        User user = objectMapper.readValue(userJsonString, User.class);

        // Verify the password
        //compare the plain text password with the encrypted password
        return passwordEncoder.matches(password, user.getPassword());
    }

    public User getVerfiedUser(String username) throws JsonMappingException, JsonProcessingException {
        String userJsonString = userRepo.getValueFromHash(ConstantVar.usersRedisKey,username.trim().toLowerCase());
        User user = objectMapper.readValue(userJsonString, User.class);
        return user;
    }
    
}
